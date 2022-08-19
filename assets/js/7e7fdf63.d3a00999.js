"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[254],{3905:(e,t,r)=>{r.d(t,{Zo:()=>p,kt:()=>m});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function l(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var c=n.createContext({}),s=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},p=function(e){var t=s(e.components);return n.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,c=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),d=s(r),m=o,v=d["".concat(c,".").concat(m)]||d[m]||u[m]||a;return r?n.createElement(v,i(i({ref:t},p),{},{components:r})):n.createElement(v,i({ref:t},p))}));function m(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=d;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:o,i[1]=l;for(var s=2;s<a;s++)i[s]=r[s];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}d.displayName="MDXCreateElement"},1471:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>u,frontMatter:()=>a,metadata:()=>l,toc:()=>s});var n=r(7462),o=(r(7294),r(3905));const a={id:"overview_usage",title:"Usage"},i=void 0,l={unversionedId:"overview/overview_usage",id:"overview/overview_usage",title:"Usage",description:"URLs",source:"@site/docs/overview/overview_usage.md",sourceDirName:"overview",slug:"/overview/overview_usage",permalink:"/scurl-detector/overview/overview_usage",draft:!1,editUrl:"https://github.com/lambdaworks/scurl-detector/edit/main/website/docs/overview/overview_usage.md",tags:[],version:"current",frontMatter:{id:"overview_usage",title:"Usage"},sidebar:"overview_sidebar",previous:{title:"Overview",permalink:"/scurl-detector/overview/overview_index"},next:{title:"Example",permalink:"/scurl-detector/overview/overview_example"}},c={},s=[{value:"URLs",id:"urls",level:2},{value:"UrlDetector",id:"urldetector",level:2},{value:"UrlDetectorOptions",id:"urldetectoroptions",level:2},{value:"Extracting",id:"extracting",level:2}],p={toc:s};function u(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},p,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"urls"},"URLs"),(0,o.kt)("p",null,"This library uses the ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/lemonlabsuk/scala-uri"},"scala-uri")," library for representing URLs."),(0,o.kt)("h2",{id:"urldetector"},"UrlDetector"),(0,o.kt)("p",null,"To use the Scala URL Detector library, you need to import the ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector")," class:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import io.lambdaworks.detection.UrlDetector\n")),(0,o.kt)("p",null,"An ",(0,o.kt)("inlineCode",{parentName:"p"},"apply")," method is defined inside the companion object for instantiating a ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"object UrlDetector {\n\n  def apply(options: UrlDetectorOptions, allowedOption: Option[Set[Host]], deniedOption: Option[Set[Host]]): UrlDetector\n  \n}\n")),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"allowed")," represents an optional set of hosts of URLs which the detector is supposed to detect, while ",(0,o.kt)("inlineCode",{parentName:"p"},"denied")," specifies an optional set of hosts of URLs which the detector should ignore. You don't have to specify a www subdomain, as it is assumed."),(0,o.kt)("p",null,"If you want to instantiate a ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector")," with the default configuration, you can use ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector.default"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"object UrlDetector {\n\n  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default, None, None)\n\n}\n")),(0,o.kt)("p",null,"You can create a new ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector")," from an existing one using the following ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector")," methods:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"def withOptions(options: UrlDetectorOptions): UrlDetector\n\ndef withAllowed(allowed: Set[Host]): UrlDetector\n\ndef withDenied(denied: Set[Host]): UrlDetector \n")),(0,o.kt)("h2",{id:"urldetectoroptions"},"UrlDetectorOptions"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetectorOptions")," is a Sum type, with all the case objects defined in the ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala"},"UrlDetectorOptions.scala")," file."),(0,o.kt)("h2",{id:"extracting"},"Extracting"),(0,o.kt)("p",null,"In order to extract URLs from a ",(0,o.kt)("inlineCode",{parentName:"p"},"String")," using an instance of ",(0,o.kt)("inlineCode",{parentName:"p"},"UrlDetector"),", you need to call the ",(0,o.kt)("inlineCode",{parentName:"p"},"extract")," method with that ",(0,o.kt)("inlineCode",{parentName:"p"},"String"),", which will return ",(0,o.kt)("inlineCode",{parentName:"p"},"Set[AbsoluteUrl]"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"def extract(content: String): Set[AbsoluteUrl]\n")),(0,o.kt)("p",null,"If a URL inside the specified ",(0,o.kt)("inlineCode",{parentName:"p"},"content")," doesn't have a scheme specified, it will be returned with a http scheme."))}u.isMDXComponent=!0}}]);