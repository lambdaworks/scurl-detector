"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[673],{2688:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>l,contentTitle:()=>c,default:()=>h,frontMatter:()=>i,metadata:()=>s,toc:()=>a});const s=JSON.parse('{"id":"overview/overview_usage","title":"Usage","description":"URLs","source":"@site/../scurl-detector-docs/target/mdoc/overview/overview_usage.md","sourceDirName":"overview","slug":"/overview/overview_usage","permalink":"/scurl-detector/overview/overview_usage","draft":false,"unlisted":false,"editUrl":"https://github.com/lambdaworks/scurl-detector/edit/main/docs/overview/overview_usage.md","tags":[],"version":"current","frontMatter":{"id":"overview_usage","title":"Usage"},"sidebar":"docs","previous":{"title":"Overview","permalink":"/scurl-detector/overview/overview_index"},"next":{"title":"Example","permalink":"/scurl-detector/overview/overview_example"}}');var o=r(4848),n=r(8453);const i={id:"overview_usage",title:"Usage"},c=void 0,l={},a=[{value:"URLs",id:"urls",level:2},{value:"UrlDetector",id:"urldetector",level:2},{value:"UrlDetectorOptions",id:"urldetectoroptions",level:2},{value:"Extracting",id:"extracting",level:2}];function d(e){const t={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,n.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(t.h2,{id:"urls",children:"URLs"}),"\n",(0,o.jsxs)(t.p,{children:["This library uses the ",(0,o.jsx)(t.a,{href:"https://github.com/lemonlabsuk/scala-uri",children:"scala-uri"})," library for representing URLs."]}),"\n",(0,o.jsx)(t.h2,{id:"urldetector",children:"UrlDetector"}),"\n",(0,o.jsxs)(t.p,{children:["To use the Scala URL Detector library, you need to import the ",(0,o.jsx)(t.code,{children:"UrlDetector"})," class:"]}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-scala",children:"import io.lambdaworks.detection.UrlDetector\n"})}),"\n",(0,o.jsxs)(t.p,{children:["An ",(0,o.jsx)(t.code,{children:"apply"})," method is defined inside the companion object for instantiating a ",(0,o.jsx)(t.code,{children:"UrlDetector"}),":"]}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-scala",children:"object UrlDetector {\n\n  def apply(options: UrlDetectorOptions): UrlDetector\n  \n}\n"})}),"\n",(0,o.jsxs)(t.p,{children:["Where ",(0,o.jsx)(t.code,{children:"options"})," specify the configuration of the ",(0,o.jsx)(t.code,{children:"UrlDetector"}),"."]}),"\n",(0,o.jsxs)(t.p,{children:["If you want to instantiate a ",(0,o.jsx)(t.code,{children:"UrlDetector"})," with the default configuration, you can use ",(0,o.jsx)(t.code,{children:"UrlDetector.default"}),":"]}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-scala",children:"object UrlDetector {\n\n  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default)\n\n}\n"})}),"\n",(0,o.jsxs)(t.p,{children:["You can create a new ",(0,o.jsx)(t.code,{children:"UrlDetector"})," from an existing one using the following ",(0,o.jsx)(t.code,{children:"UrlDetector"})," methods:"]}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-scala",children:"def withAllowed(host: Host, hosts: Host*): UrlDetector\n\ndef withDenied(host: Host, hosts: Host*): UrlDetector\n\ndef withOptions(options: UrlDetectorOptions): UrlDetector\n"})}),"\n",(0,o.jsxs)(t.p,{children:["Where with ",(0,o.jsx)(t.code,{children:"withAllowed"})," we specify hosts of URLs which the detector is supposed to detect, while with ",(0,o.jsx)(t.code,{children:"withDenied"})," we specify hosts of URLs which the detector should ignore. You don't have to specify a www subdomain for hosts, as it is assumed. Unless another subdomain is specified, all possible subdomains will be matched."]}),"\n",(0,o.jsx)(t.h2,{id:"urldetectoroptions",children:"UrlDetectorOptions"}),"\n",(0,o.jsxs)(t.p,{children:[(0,o.jsx)(t.code,{children:"UrlDetectorOptions"})," is a Sum type, with all the case objects defined in the ",(0,o.jsx)(t.a,{href:"https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala",children:"UrlDetectorOptions.scala"})," file."]}),"\n",(0,o.jsx)(t.h2,{id:"extracting",children:"Extracting"}),"\n",(0,o.jsxs)(t.p,{children:["In order to extract URLs from a ",(0,o.jsx)(t.code,{children:"String"})," using an instance of ",(0,o.jsx)(t.code,{children:"UrlDetector"}),", you need to call the ",(0,o.jsx)(t.code,{children:"extract"})," method with that ",(0,o.jsx)(t.code,{children:"String"}),", which will return ",(0,o.jsx)(t.code,{children:"Set[AbsoluteUrl]"}),":"]}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-scala",children:"def extract(content: String): Set[AbsoluteUrl]\n"})}),"\n",(0,o.jsxs)(t.p,{children:["If a URL inside the specified ",(0,o.jsx)(t.code,{children:"content"})," doesn't have a scheme specified, it will be returned with a http scheme."]})]})}function h(e={}){const{wrapper:t}={...(0,n.R)(),...e.components};return t?(0,o.jsx)(t,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},8453:(e,t,r)=>{r.d(t,{R:()=>i,x:()=>c});var s=r(6540);const o={},n=s.createContext(o);function i(e){const t=s.useContext(n);return s.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:i(e.components),s.createElement(n.Provider,{value:t},e.children)}}}]);