import{u as V,b as M}from"./vue-router-8555237b.js";import{_ as j}from"./index-ebfb4caf.js";import{j as E,r as g,ag as v,o as a,c as u,a as s,V as h,P as d,T as k,O as y,F as C,S as f,U as T,u as P,bh as w,bf as U}from"./@vue-57ff52f9.js";import"./pinia-00fcc55c.js";import"./spark-md5-52dc0e70.js";import"./APlayer-9c029590.js";import"./element-plus-61115a40.js";import"./lodash-es-36eb724a.js";import"./@vueuse-7d05ada6.js";import"./@element-plus-b7409ee9.js";import"./@popperjs-c75af06c.js";import"./@ctrl-f8748455.js";import"./dayjs-0d8d1de0.js";import"./async-validator-10c6301d.js";import"./memoize-one-297ddbcb.js";import"./escape-html-a32e42c6.js";import"./normalize-wheel-es-ed76fb12.js";import"./@floating-ui-880a26aa.js";import"./vue-cookies-10167fa5.js";import"./@highlightjs-be8e8472.js";import"./highlight.js-c8ccfcef.js";import"./axios-4a70c6fc.js";import"./docx-preview-6b5e4251.js";import"./jszip-97c1c277.js";import"./xlsx-04f2268f.js";import"./vue-pdf-embed-c0d60ac6.js";import"./vue-68235e8f.js";import"./vue3-pdfjs-debe5ed4.js";import"./pdfjs-dist-b6840bcc.js";import"./dommatrix-6b05c112.js";import"./web-streams-polyfill-dc7eb50b.js";import"./dplayer-a768f587.js";import"./vue-clipboard3-adec9f65.js";import"./clipboard-de925a1a.js";const I=p=>(w("data-v-43c338fa"),p=p(),U(),p),H={class:"top"},A=I(()=>s("span",{class:"iconfont icon-revert"},null,-1)),G=I(()=>s("span",{class:"iconfont icon-del"},null,-1)),J={class:"file-list"},K=["onMouseenter","onMouseleave"],Q=["title"],W={class:"op"},X=["onClick"],Y=["onClick"],Z={key:0},ee={__name:"Recycle",emits:["reload"],setup(p,{emit:S}){const{proxy:o}=E();V(),M();const n={loadDataList:"/recycle/loadRecycleList",delFile:"/recycle/delFile",recoverFile:"/recycle/recoverFile"},N=[{label:"文件名",prop:"fileName",scopedSlots:"fileName"},{label:"删除时间",prop:"recoveryTime",width:200},{label:"大小",prop:"fileSize",scopedSlots:"fileSize",width:200}],c=g({}),F={extHeight:20,selectType:"checkbox"},r=async()=>{let e={pageNo:c.value.pageNo||"1",pageSize:c.value.pageSize||"15"};e.category!=="all"&&delete e.filePid;let l=await o.Request({url:n.loadDataList,params:e});l&&(c.value=l.data)},R=e=>{c.value.list.forEach(l=>{l.showOp=!1}),e.showOp=!0},x=e=>{e.showOp=!1},i=g([]),z=e=>{i.value=[],e.forEach(l=>{i.value.push(l.fileId)})},O=e=>{o.Confirm(`你确定要还原【${e.fileName}】吗？`,async()=>{await o.Request({url:n.recoverFile,params:{fileIds:e.fileId}})&&r()})},B=()=>{i.value.length!=0&&o.Confirm("你确定要还原这些文件吗？",async()=>{await o.Request({url:n.recoverFile,params:{fileIds:i.value.join(",")}})&&r()})},$=e=>{o.Confirm(`你确定要删除【${e.fileName}】？`,async()=>{await o.Request({url:n.delFile,params:{fileIds:e.fileId}})&&(r(),S("reload"))})},q=e=>{i.value.length!=0&&o.Confirm("你确定要删除选中的文件?删除将无法恢复",async()=>{await o.Request({url:n.delFile,params:{fileIds:i.value.join(",")}})&&(r(),S("reload"))})};return(e,l)=>{const b=v("el-button"),m=v("icon"),D=v("Table");return a(),u("div",null,[s("div",H,[h(b,{type:"success",disabled:i.value.length==0,onClick:B},{default:d(()=>[A,k("还原 ")]),_:1},8,["disabled"]),h(b,{type:"danger",disabled:i.value.length==0,onClick:q},{default:d(()=>[G,k("批量删除 ")]),_:1},8,["disabled"])]),s("div",J,[h(D,{columns:N,showPagination:!0,dataSource:c.value,fetch:r,options:F,onRowSelected:z},{fileName:d(({index:L,row:t})=>[s("div",{class:"file-item",onMouseenter:_=>R(t),onMouseleave:_=>x(t)},[(t.fileType==3||t.fileType==1)&&t.status!==0?(a(),y(m,{key:0,cover:t.fileCover},null,8,["cover"])):(a(),u(C,{key:1},[t.folderType==0?(a(),y(m,{key:0,fileType:t.fileType},null,8,["fileType"])):f("",!0),t.folderType==1?(a(),y(m,{key:1,fileType:0})):f("",!0)],64)),s("span",{class:"file-name",title:t.fileName},[s("span",null,T(t.fileName),1)],8,Q),s("span",W,[t.showOp&&t.fileId?(a(),u(C,{key:0},[s("span",{class:"iconfont icon-revert",onClick:_=>O(t)},"还原",8,X),s("span",{class:"iconfont icon-del",onClick:_=>$(t)},"删除",8,Y)],64)):f("",!0)])],40,K)]),fileSize:d(({index:L,row:t})=>[t.fileSize?(a(),u("span",Z,T(P(o).Utils.sizeToStr(t.fileSize)),1)):f("",!0)]),_:1},8,["dataSource"])])])}}},De=j(ee,[["__scopeId","data-v-43c338fa"]]);export{De as default};
