import{j as O,r as m,ag as v,o as n,c as _,a as s,F as j,V as r,P as d,T as G,a3 as Y,U as D,n as Z,a8 as H,O as B,S as z,J as L,K as W,bh as ee,bf as te,w as ie,u as X,R as ue}from"./@vue-57ff52f9.js";import{u as E,b as J}from"./vue-router-8555237b.js";import{_ as se}from"./index-ebfb4caf.js";import{S as ce}from"./spark-md5-52dc0e70.js";import"./pinia-00fcc55c.js";import"./element-plus-61115a40.js";import"./APlayer-9c029590.js";import"./lodash-es-36eb724a.js";import"./@vueuse-7d05ada6.js";import"./@element-plus-b7409ee9.js";import"./@popperjs-c75af06c.js";import"./@ctrl-f8748455.js";import"./dayjs-0d8d1de0.js";import"./async-validator-10c6301d.js";import"./memoize-one-297ddbcb.js";import"./escape-html-a32e42c6.js";import"./normalize-wheel-es-ed76fb12.js";import"./@floating-ui-880a26aa.js";import"./vue-cookies-10167fa5.js";import"./@highlightjs-be8e8472.js";import"./highlight.js-c8ccfcef.js";import"./axios-4a70c6fc.js";import"./docx-preview-6b5e4251.js";import"./jszip-97c1c277.js";import"./xlsx-04f2268f.js";import"./vue-pdf-embed-c0d60ac6.js";import"./vue-68235e8f.js";import"./vue3-pdfjs-debe5ed4.js";import"./pdfjs-dist-b6840bcc.js";import"./dommatrix-6b05c112.js";import"./web-streams-polyfill-dc7eb50b.js";import"./dplayer-a768f587.js";import"./vue-clipboard3-adec9f65.js";import"./clipboard-de925a1a.js";const de={class:"avatar-upload"},pe={class:"avatar-show"},me=["src"],_e=["src"],fe=["src"],ve={class:"select-btn"},he={__name:"AvatarUpload",props:{modelValue:{type:Object,default:null}},setup(g,{emit:k}){O(),E(),J(),m("");const $=m(null),A=async t=>{t=t.file;let i=new FileReader;i.readAsDataURL(t),i.onload=({target:h})=>{$.value=h.result},k("update:modelValue",t)};return(t,i)=>{const h=v("el-button"),N=v("el-upload");return n(),_("div",de,[s("div",pe,[$.value?(n(),_("img",{key:0,src:$.value},null,8,me)):(n(),_(j,{key:1},[g.modelValue&&g.modelValue.qqAvatar?(n(),_("img",{key:0,src:`${g.modelValue.qqAvatar}`},null,8,_e)):(n(),_("img",{key:1,src:`/api/getAvatar/${g.modelValue.userId}`},null,8,fe))],64))]),s("div",ve,[r(N,{name:"file","show-file-list":!1,accept:".png,.PNG,.jpg,.JPG,.jpeg,.JPEG,.gif,.GIF,.bmp,.BMP",multiple:!1,"http-request":A},{default:d(()=>[r(h,{type:"primary"},{default:d(()=>[G("选择")]),_:1})]),_:1})])])}}},we={__name:"UpdateAvatar",emits:["updateAvatar"],setup(g,{expose:k,emit:$}){const{proxy:A}=O();E(),J();const t={updateUserAvatar:"updateUserAvatar"},i=m({}),h=m();k({show:F=>{i.value=Object.assign({},F),i.value.avatar={userId:F.userId,qqAvatar:F.avatar},P.value.show=!0}});const P=m({show:!1,title:"修改头像",buttons:[{type:"primary",text:"确定",click:F=>{R()}}]}),R=async()=>{if(!(i.value.avatar instanceof File)){P.value.show=!1;return}if(!await A.Request({url:t.updateUserAvatar,params:{avatar:i.value.avatar}}))return;P.value.show=!1;const p=A.VueCookies.get("userInfo");delete p.avatar,A.VueCookies.set("userInfo",p,0),$("updateAvatar")};return(F,p)=>{const f=v("el-form-item"),y=v("el-form"),b=v("Dialog");return n(),_("div",null,[r(b,{show:P.value.show,title:P.value.title,buttons:P.value.buttons,width:"500px",showCancel:!0,onClose:p[2]||(p[2]=a=>P.value.show=!1)},{default:d(()=>[r(y,{model:i.value,ref_key:"formDataRef",ref:h,"label-width":"80px",onSubmit:p[1]||(p[1]=Y(()=>{},["prevent"]))},{default:d(()=>[r(f,{label:"昵称",prop:""},{default:d(()=>[G(D(i.value.nickName),1)]),_:1}),r(f,{label:"头像",prop:""},{default:d(()=>[r(he,{modelValue:i.value.avatar,"onUpdate:modelValue":p[0]||(p[0]=a=>i.value.avatar=a)},null,8,["modelValue"])]),_:1})]),_:1},8,["model"])]),_:1},8,["show","title","buttons"])])}}},ge=s("span",{class:"iconfont icon-password"},null,-1),ke=s("span",{class:"iconfont icon-password"},null,-1),ye={__name:"UpdatePassword",setup(g,{expose:k}){const{proxy:$}=O();E(),J();const A={updatePassword:"updatePassword"},t=m({}),i=m(),h=(p,f,y)=>{f!==t.value.rePassword?y(new Error(p.message)):y()},N={password:[{required:!0,message:"请输入密码"},{validator:$.Verify.password,message:"密码只能是数字，字母，特殊字符 8-18位"}],rePassword:[{required:!0,message:"请再次输入密码"},{validator:h,message:"两次输入的密码不一致"}]};k({show:()=>{R.value.show=!0,Z(()=>{i.value.resetFields(),t.value={}})}});const R=m({show:!1,title:"修改密码",buttons:[{type:"primary",text:"确定",click:p=>{F()}}]}),F=async()=>{i.value.validate(async p=>{!p||!await $.Request({url:A.updatePassword,params:{password:t.value.password}})||(R.value.show=!1,$.message.success("密码修改成功"))})};return(p,f)=>{const y=v("el-input"),b=v("el-form-item"),a=v("el-form"),l=v("Dialog");return n(),_("div",null,[r(l,{show:R.value.show,title:R.value.title,buttons:R.value.buttons,width:"500px",showCancel:!0,onClose:f[3]||(f[3]=e=>R.value.show=!1)},{default:d(()=>[r(a,{model:t.value,rules:N,ref_key:"formDataRef",ref:i,"label-width":"80px",onSubmit:f[2]||(f[2]=Y(()=>{},["prevent"]))},{default:d(()=>[r(b,{label:"新密码",prop:"password"},{default:d(()=>[r(y,{type:"password",size:"large",placeholder:"请输入密码",modelValue:t.value.password,"onUpdate:modelValue":f[0]||(f[0]=e=>t.value.password=e),"show-password":""},{prefix:d(()=>[ge]),_:1},8,["modelValue"])]),_:1}),r(b,{label:"确认密码",prop:"rePassword"},{default:d(()=>[r(y,{type:"password",size:"large",placeholder:"请再次输入密码",modelValue:t.value.rePassword,"onUpdate:modelValue":f[1]||(f[1]=e=>t.value.rePassword=e),"show-password":""},{prefix:d(()=>[ke]),_:1},8,["modelValue"])]),_:1})]),_:1},8,["model"])]),_:1},8,["show","title","buttons"])])}}},Ce=g=>(ee("data-v-412dac64"),g=g(),te(),g),be={class:"uploader-panel"},Se=Ce(()=>s("div",{class:"uploader-title"},[s("span",null,"上传任务"),s("span",{class:"tips"},"（仅展示本次上传任务）")],-1)),xe={class:"file-list"},$e={class:"file-item"},Fe={class:"upload-panel"},Pe={class:"file-name"},Ue={class:"progress"},Ie={class:"upload-status"},Re={key:0,class:"upload-info"},Ve={class:"op"},Ae={class:"op-btn"},De={key:0},ze={key:0},M=1024*1024*5,Ne={__name:"Uploader",emits:["uploadCallback"],setup(g,{expose:k,emit:$}){const{proxy:A}=O(),t={emptyfile:{value:"emptyfile",desc:"文件为空",color:"#F75000",icon:"close"},fail:{value:"fail",desc:"上传失败",color:"#F75000",icon:"close"},init:{value:"init",desc:"解析中",color:"#e6a23c",icon:"clock"},uploading:{value:"uploading",desc:"上传中",color:"#409eff",icon:"upload"},upload_finish:{value:"upload_finish",desc:"上传完成",color:"#67c23a",icon:"ok"},upload_seconds:{value:"upload_seconds",desc:"秒传",color:"#67c23a",icon:"ok"}},i=m([]),h=m([]);k({addFile:async(a,l)=>{const e={file:a,uid:a.uid,md5Progress:0,md5:null,fileName:a.name,status:t.init.value,uploadSize:0,totalSize:a.size,uploadProgress:0,pause:!1,chunkIndex:0,filePid:l,errorMsg:null};if(i.value.unshift(e),e.totalSize==0){e.status=t.emptyfile.value;return}let c=await f(e);c!=null&&p(c)}});const P=a=>{let l=y(a);l.pause=!1,p(a,l.chunkIndex)},R=a=>{let l=y(a);l.pause=!0},F=(a,l)=>{h.value.push(a),i.value.splice(l,1)},p=async(a,l)=>{l=l||0;let e=y(a);const c=e.file,w=e.totalSize,o=Math.ceil(w/M);for(let S=l;S<o;S++){let V=h.value.indexOf(a);if(V!=-1){h.value.splice(V,1);break}if(e=y(a),e.pause)break;let U=S*M,T=U+M>=w?w:U+M,u=c.slice(U,T),x=await A.Request({url:"/file/uploadFile",showLoading:!1,dataType:"file",params:{file:u,fileName:c.name,fileMd5:e.md5,chunkIndex:S,chunks:o,fileId:e.fileId,filePid:e.filePid},showError:!1,errorCallback:I=>{e.status=t.fail.value,e.errorMsg=I},uploadProgressCallback:I=>{let q=I.loaded;q>w&&(q=w),e.uploadSize=S*M+q,e.uploadProgress=Math.floor(e.uploadSize/w*100)}});if(x==null)break;if(e.fileId=x.data.fileId,e.status=t[x.data.status].value,e.chunkIndex=S,x.data.status==t.upload_seconds.value||x.data.status==t.upload_finish.value){e.uploadProgress=100,$("uploadCallback");break}}},f=a=>{let l=a.file,e=File.prototype.slice||File.prototype.mozSlice||File.prototype.webkitSlice,c=Math.ceil(l.size/M),w=0,o=new ce.ArrayBuffer,S=new FileReader;new Date().getTime();let V=()=>{let U=w*M,T=U+M>=l.size?l.size:U+M;S.readAsArrayBuffer(e.call(l,U,T))};return V(),new Promise((U,T)=>{let u=y(l.uid);S.onload=x=>{if(o.append(x.target.result),w++,w<c){let I=Math.floor(w/c*100);u.md5Progress=I,V()}else{let I=o.end();o.destroy(),u.md5Progress=100,u.status=t.uploading.value,u.md5=I,U(a.uid)}},S.onerror=()=>{u.md5Progress=-1,u.status=t.fail.value,U(a.uid)}}).catch(U=>null)},y=a=>i.value.find(e=>e.file.uid===a),b=a=>{var l="";a<.1*1024?l=a.toFixed(2)+"B":a<.1*1024*1024?l=(a/1024).toFixed(2)+"KB":a<1024*1024*1024?l=(a/(1024*1024)).toFixed(2)+"MB":l=(a/(1024*1024*1024)).toFixed(2)+"GB";var e=l+"",c=e.indexOf("."),w=e.substr(c+1,2);return w=="00"?e.substring(0,c)+e.substr(c+3,2):e};return(a,l)=>{const e=v("el-progress"),c=v("icon"),w=v("NoData");return n(),_("div",be,[Se,s("div",xe,[(n(!0),_(j,null,H(i.value,(o,S)=>(n(),_("div",$e,[s("div",Fe,[s("div",Pe,D(o.fileName),1),s("div",Ue,[o.status==t.uploading.value||o.status==t.upload_seconds.value||o.status==t.upload_finish.value?(n(),B(e,{key:0,percentage:o.uploadProgress},null,8,["percentage"])):z("",!0)]),s("div",Ie,[s("span",{class:L(["iconfont","icon-"+t[o.status].icon]),style:W({color:t[o.status].color})},null,6),s("span",{class:"status",style:W({color:t[o.status].color})},D(o.status=="fail"?o.errorMsg:t[o.status].desc),5),o.status==t.uploading.value?(n(),_("span",Re,D(b(o.uploadSize))+"/"+D(b(o.totalSize)),1)):z("",!0)])]),s("div",Ve,[o.status==t.init.value?(n(),B(e,{key:0,type:"circle",width:50,percentage:o.md5Progress},null,8,["percentage"])):z("",!0),s("div",Ae,[o.status===t.uploading.value?(n(),_("span",De,[o.pause?(n(),B(c,{key:0,width:28,class:"btn-item",iconName:"upload",title:"上传",onClick:V=>P(o.uid)},null,8,["onClick"])):(n(),B(c,{key:1,width:28,class:"btn-item",iconName:"pause",title:"暂停",onClick:V=>R(o.uid)},null,8,["onClick"]))])):z("",!0),o.status!=t.init.value&&o.status!=t.upload_finish.value&&o.status!=t.upload_seconds.value?(n(),B(c,{key:1,width:28,class:"del btn-item",iconName:"del",title:"删除",onClick:V=>F(o.uid,S)},null,8,["onClick"])):z("",!0),o.status==t.upload_finish.value||o.status==t.upload_seconds.value?(n(),B(c,{key:2,width:28,class:"clean btn-item",iconName:"clean",title:"清除",onClick:V=>F(o.uid,S)},null,8,["onClick"])):z("",!0)])])]))),256)),i.value.length==0?(n(),_("div",ze,[r(w,{msg:"暂无上传任务"})])):z("",!0)])])}}},Me=se(Ne,[["__scopeId","data-v-412dac64"]]),Q=g=>(ee("data-v-24220840"),g=g(),te(),g),qe={class:"framework"},Be={class:"header"},Te=Q(()=>s("div",{class:"logo"},[s("span",{class:"iconfont icon-pan"}),s("span",{class:"name"},"Small云盘")],-1)),Le={class:"right-panel"},je=Q(()=>s("span",{class:"iconfont icon-transfer"},null,-1)),Ge={class:"user-info"},Oe={class:"avatar"},Ee={class:"nick-name"},Je={class:"body"},Ke={class:"left-sider"},He={class:"menu-list"},Qe=["onClick"],We={class:"text"},Xe={class:"menu-sub-list"},Ye=["onClick"],Ze={class:"text"},et={key:0,class:"tips"},tt={class:"space-info"},st=Q(()=>s("div",null,"空间使用",-1)),at={class:"percent"},ot={class:"space-use"},lt={class:"use"},nt={class:"body-content"},rt={__name:"Framework",setup(g){const{proxy:k}=O(),$=E(),A=J(),t={getUseSpace:"/getUseSpace",logout:"/logout"},i=m(0),h=m(k.VueCookies.get("userInfo")),N=m(!1),P=m(),R=u=>{const{file:x,filePid:I}=u;N.value=!0,P.value.addFile(x,I)},F=m(),p=()=>{Z(()=>{F.value.reload(),c()})},f=[{icon:"cloude",name:"首页",menuCode:"main",path:"/main/all",allShow:!0,children:[{icon:"all",name:"全部",category:"all",path:"/main/all"},{icon:"video",name:"视频",category:"video",path:"/main/video"},{icon:"music",name:"音频",category:"music",path:"/main/music"},{icon:"image",name:"图片",category:"image",path:"/main/image"},{icon:"doc",name:"文档",category:"doc",path:"/main/doc"},{icon:"more",name:"其他",category:"others",path:"/main/others"}]},{path:"/myshare",icon:"share",name:"分享",menuCode:"share",allShow:!0,children:[{name:"分享记录",path:"/myshare"}]},{path:"/recycle",icon:"del",name:"回收站",menuCode:"recycle",tips:"回收站为你保存10天内删除的文件",allShow:!0,children:[{name:"删除的文件",path:"/recycle"}]},{path:"/settings/fileList",icon:"settings",name:"设置",menuCode:"settings",allShow:!1,children:[{name:"用户文件",path:"/settings/fileList"},{name:"用户管理",path:"/settings/userList"}]}],y=u=>{!u.path||u.menuCode==b.value.menuCode||$.push(u.path)},b=m({}),a=m(),l=(u,x)=>{const I=f.find(q=>q.menuCode===u);b.value=I,a.value=x};ie(()=>A,(u,x)=>{u.meta.menuCode&&l(u.meta.menuCode,u.path)},{immediate:!0,deep:!0});const e=m({useSpace:0,totalSpace:1}),c=async()=>{let u=await k.Request({url:t.getUseSpace,showLoading:!1});u&&(e.value=u.data)};c();const w=m(),o=()=>{w.value.show(h.value)},S=()=>{h.value=k.VueCookies.get("userInfo"),i.value=new Date().getTime()},V=m(),U=()=>{V.value.show()},T=()=>{k.Confirm("你确定要删除退出吗",async()=>{await k.Request({url:t.logout})&&(k.VueCookies.remove,$.push("/login"))})};return(u,x)=>{const I=v("el-popover"),q=v("Avatar"),K=v("el-dropdown-item"),ae=v("el-dropdown-menu"),oe=v("el-dropdown"),le=v("el-progress"),ne=v("router-view");return n(),_("div",qe,[s("div",Be,[Te,s("div",Le,[r(I,{width:800,trigger:"click",visible:N.value,"onUpdate:visible":x[0]||(x[0]=C=>N.value=C),offset:20,transition:"none","hide-after":0,"popper-style":{padding:"0px"}},{reference:d(()=>[je]),default:d(()=>[r(Me,{ref_key:"uploaderRef",ref:P,onUploadCallback:p},null,512)]),_:1},8,["visible"]),r(oe,null,{dropdown:d(()=>[r(ae,null,{default:d(()=>[r(K,{onClick:o,class:"message-item"},{default:d(()=>[G(" 修改头像 ")]),_:1}),r(K,{onClick:U,class:"message-item"},{default:d(()=>[G(" 修改密码 ")]),_:1}),r(K,{onClick:T,class:"message-item"},{default:d(()=>[G(" 退出 ")]),_:1})]),_:1})]),default:d(()=>[s("div",Ge,[s("div",Oe,[r(q,{userId:h.value.userId,avatar:h.value.avatar,timestamp:i.value,width:46},null,8,["userId","avatar","timestamp"])]),s("span",Ee,D(h.value.nickName),1)])]),_:1})])]),s("div",Je,[s("div",Ke,[s("div",He,[(n(),_(j,null,H(f,C=>s("div",{onClick:re=>y(C),class:L(["menu-item",C.menuCode==b.value.menuCode?"active":""])},[C.allShow||!C.allShow&&h.value.isAdmin?(n(),_(j,{key:0},[s("div",{class:L(["iconfont","icon-"+C.icon])},null,2),s("div",We,D(C.name),1)],64)):z("",!0)],10,Qe)),64))]),s("div",Xe,[(n(!0),_(j,null,H(b.value.children,C=>(n(),_("div",{onClick:re=>y(C),class:L(["menu-item-sub",a.value==C.path?"active":""])},[C.icon?(n(),_("span",{key:0,class:L(["iconfont","icon-"+C.icon])},null,2)):z("",!0),s("span",Ze,D(C.name),1)],10,Ye))),256)),b.value&&b.value.tips?(n(),_("div",et,D(b.value.tips),1)):z("",!0),s("div",tt,[st,s("div",at,[r(le,{percentage:Math.floor(e.value.useSpace/e.value.totalSpace*1e4)/100,color:"#409eff"},null,8,["percentage"])]),s("div",ot,[s("div",lt,D(X(k).Utils.sizeToStr(e.value.useSpace))+"/ "+D(X(k).Utils.sizeToStr(e.value.totalSpace)),1),s("div",{class:"iconfont icon-refresh",onClick:c})])])])]),s("div",nt,[r(ne,null,{default:d(({Component:C})=>[(n(),B(ue(C),{onAddFile:R,ref_key:"routerViewRef",ref:F,onReload:c},null,544))]),_:1})])]),r(we,{ref_key:"updateAvatarRef",ref:w,onUpdateAvatar:S},null,512),r(ye,{ref_key:"updatePasswordRef",ref:V},null,512)])}}},jt=se(rt,[["__scopeId","data-v-24220840"]]);export{jt as default};
