const root=document.getElementById("editor");
const paintLayer=document.getElementById("paint-layer");
const paintCtx=paintLayer.getContext("2d",{willReadFrequently:true});
const viewerCanvas=document.createElement("canvas");
document.getElementById("skin-container").appendChild(viewerCanvas);

let viewer=new skinview3d.SkinViewer({
  canvas:viewerCanvas,
  width:root.clientWidth||360,
  height:root.clientHeight||390
});
viewer.controls.enableZoom=false;
viewer.controls.enablePan=false;
viewer.controls.enableRotate=false;
viewer.animation=null;
viewer.globalLight.intensity=3;
viewer.cameraLight.intensity=1;
viewer.zoom=0.85;

const textureCanvas=document.createElement("canvas");
textureCanvas.width=64;
textureCanvas.height=64;
const textureCtx=textureCanvas.getContext("2d",{willReadFrequently:true});
let paintColor="#FFFFFF";
let skinReady=false;
let drawing=false;
let lastPoint=null;

function resize(){
  const w=root.clientWidth||360,h=root.clientHeight||390;
  viewer.width=w; viewer.height=h;
  paintLayer.width=w; paintLayer.height=h;
  paintLayer.style.width=w+"px"; paintLayer.style.height=h+"px";
}
window.addEventListener("resize",resize);
resize();

function loadEditorSkin(dataUrl){
  const img=new Image();
  img.onload=()=>{
    textureCtx.clearRect(0,0,64,64);
    textureCtx.drawImage(img,0,0,64,64);
    viewer.loadSkin(textureCanvas);
    skinReady=true;
  };
  img.src=dataUrl;
}

function setPaintColor(hex){paintColor=hex}

function frontFaceAt(x,y){
  const w=paintLayer.width,h=paintLayer.height;
  const nx=x/w, ny=y/h;
  let r=null;

  if(nx>=.37&&nx<=.63&&ny>=.08&&ny<=.30){
    r={u:8,v:8,w:8,h:8};
  }else if(nx>=.39&&nx<=.61&&ny>.29&&ny<=.63){
    r={u:20,v:20,w:8,h:12};
  }else if(nx>=.25&&nx<.40&&ny>.30&&ny<=.64){
    r={u:44,v:20,w:4,h:12};
  }else if(nx>.60&&nx<=.75&&ny>.30&&ny<=.64){
    r={u:36,v:52,w:4,h:12};
  }else if(nx>=.39&&nx<.50&&ny>.61&&ny<=.95){
    r={u:4,v:20,w:4,h:12};
  }else if(nx>=.50&&nx<=.61&&ny>.61&&ny<=.95){
    r={u:20,v:52,w:4,h:12};
  }
  if(!r)return null;
  const px=Math.max(0,Math.min(r.w-1,Math.floor((nx-(r===null?0:({
    u:8,v:8,w:8,h:8
  }===r?0:0)))*r.w)));
  return r;
}

function paintAt(x,y){
  if(!skinReady)return;
  const w=paintLayer.width,h=paintLayer.height;
  const nx=x/w,ny=y/h;

  const regions=[
    {sx0:.37,sx1:.63,sy0:.08,sy1:.30,u:8,v:8,uw:8,uh:8},
    {sx0:.39,sx1:.61,sy0:.29,sy1:.63,u:20,v:20,uw:8,uh:12},
    {sx0:.25,sx1:.40,sy0:.30,sy1:.64,u:44,v:20,uw:4,uh:12},
    {sx0:.60,sx1:.75,sy0:.30,sy1:.64,u:36,v:52,uw:4,uh:12},
    {sx0:.39,sx1:.50,sy0:.61,sy1:.95,u:4,v:20,uw:4,uh:12},
    {sx0:.50,sx1:.61,sy0:.61,sy1:.95,u:20,v:52,uw:4,uh:12}
  ];

  const region=regions.find(r=>nx>=r.sx0&&nx<=r.sx1&&ny>=r.sy0&&ny<=r.sy1);
  if(!region)return;

  const px=Math.max(0,Math.min(region.uw-1,Math.floor((nx-region.sx0)/(region.sx1-region.sx0)*region.uw)));
  const py=Math.max(0,Math.min(region.uh-1,Math.floor((ny-region.sy0)/(region.sy1-region.sy0)*region.uh)));
  const u=Math.floor(region.u+px);
  const v=Math.floor(region.v+py);

  textureCtx.fillStyle=paintColor;
  textureCtx.fillRect(u,v,1,1);
  viewer.loadSkin(textureCanvas);
}
paintLayer.addEventListener("pointerdown",e=>{
  drawing=true; lastPoint={x:e.offsetX,y:e.offsetY};
  paintLayer.setPointerCapture(e.pointerId);
  paintAt(e.offsetX,e.offsetY);
});
paintLayer.addEventListener("pointermove",e=>{
  if(!drawing)return;
  paintAt(e.offsetX,e.offsetY);
  lastPoint={x:e.offsetX,y:e.offsetY};
});
paintLayer.addEventListener("pointerup",()=>{drawing=false;lastPoint=null});
paintLayer.addEventListener("pointercancel",()=>{drawing=false;lastPoint=null});

function exportSkin(){
  if(!skinReady)return;
  window.GrapeSkin.saveSkin(textureCanvas.toDataURL("image/png"));
}
