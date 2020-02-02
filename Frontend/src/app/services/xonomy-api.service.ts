import { Injectable } from '@angular/core';
declare const Xonomy: any;

@Injectable({
  providedIn: 'root'
})
export class XonomyApiService {
  static metadataPrefix = "pred:";
  static scientificPublicationPrefix = "http://ftn.uns.ac.rs/scientificPublication/";
  static personPrefix = "http://ftn.uns.ac.rs/person/";

  constructor() {
  }

  public scientificPublicationSpecification = {
    elements: {
      "sp:scientificPublication": {
        menu: [{
          caption: "Add RDF attribute 'about'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"about", value:XonomyApiService.scientificPublicationPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("about");
          }
        }, {
          caption: "Add RDF attribute 'typeof'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"typeof", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("typeof");
          }
        }, {
          caption: "Add <sp:metadata>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:metadata xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:metadata>"
        }, {
          caption: "Add <sp:abstract>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:abstract xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:abstract>"
        }, {
          caption: "Add <sp:chapter>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:chapter xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:chapter>"
        }, {
          caption: "Add <sp:references>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:references xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:references>"
        }],
        attributes: {
          "about": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @about",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:metadata": {
        mustBeBefore: ["sp:abstract", "sp:chapter", "sp:references"],
        menu: [{
          caption: "Add <sp:title>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:title xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:title>"
        }, {
          caption: "Add <sp:authors>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:authors xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:authors>"
        }, {
          caption: "Add <sp:keywords>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:keywords xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:keywords>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:title": {
        mustBeBefore: ["sp:authors", "sp:keywords", "sp:paragraph"],
        oneliner: true,
        hasText: true,
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"property", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "property": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @property",
              action: Xonomy.deleteAttribute
            }]
          }
        }},
        "sp:authors": {
          menu: [{
            caption: "Add <sp:author>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:author xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:author>"
          }, {
            caption: "Delete element",
            action: Xonomy.deleteElement
          }]
        },
        "sp:author": {
          menu: [{
            caption: "Add RDF attribute 'href'",
            action: Xonomy.newAttribute,
            actionParameter: {name:"href", value:XonomyApiService.personPrefix},
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("href");
            }
          }, {
            caption: "Add RDF attribute 'rel'",
            action: Xonomy.newAttribute,
            actionParameter: {name:"rel", value:XonomyApiService.metadataPrefix},
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("rel");
            }
          }, {
            caption: "Add RDF attribute 'typeof'",
            action: Xonomy.newAttribute,
            actionParameter: {name:"typeof", value:XonomyApiService.metadataPrefix},
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("typeof");
            }
          }, {
            caption: "Add <sp:name>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:name xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:name>"
          }, {
            caption: "Add <sp:affiliation>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:affiliation xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:affiliation>"
          }, {
            caption: "Add <sp:city>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:city xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:city>"
          }, {
            caption: "Add <sp:state>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:state xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:state>"
          }, {
            caption: "Add <sp:email>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:email xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:email>"
          }, {
            caption: "Delete element",
            action: Xonomy.deleteElement
          }],
          attributes: {
            "href": {
              asker: Xonomy.askString,
              menu: [{
                caption: "Delete this @href",
                action: Xonomy.deleteAttribute
              }]
            },
            "rel": {
              asker: Xonomy.askString,
              menu: [{
                caption: "Delete this @rel",
                action: Xonomy.deleteAttribute
              }]
            },
            "typeof": {
              asker: Xonomy.askString,
              menu: [{
                caption: "Delete this @typeof",
                action: Xonomy.deleteAttribute
              }]
            }
          }
        },
      "sp:name" : {
        mustBeBefore: ["sp:affiliation", "sp:city", "sp:state", "sp:email"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'about'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"about", value:XonomyApiService.personPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("about");
          }
        },{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"property", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "about": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @about",
              action: Xonomy.deleteAttribute
            }]
          },
          "property": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @property",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:affiliation" : {
        mustBeBefore: ["sp:city", "sp:state", "sp:email"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'about'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"about", value:XonomyApiService.personPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("about");
          }
        },{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"property", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "about": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @about",
              action: Xonomy.deleteAttribute
            }]
          },
          "property": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @property",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:city": {
        mustBeBefore: ["sp:state", "sp:email"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:state": {
        mustBeBefore: ["sp:email"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:email": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:keywords": {
        mustBeAfter: ["sp:title", "sp:authors"],
        menu: [{
          caption: "Add <sp:keyword>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:keyword xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:keyword>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:keyword": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu: [{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"property", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "property": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @property",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:abstract": {
        mustBeBefore: ["sp:chapter", "sp:references"],
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:paragraph>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:paragraph xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:paragraph>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:paragraph": {
        hasText: true,
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:boldText>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:boldText xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:boldText>"
        },{
          caption: "Add <sp:emphasizedText>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:emphasizedText xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:emphasizedText>"
        },{
          caption: "Add <sp:quote>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:quote xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:quote>"
        },{
          caption: "Add <sp:figure>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:figure xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:figure>"
        },{
          caption: "Add <sp:list>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:list xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:list>"
        },{
          caption: "Add <sp:code>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:code xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:code>"
        },{
          caption: "Add <sp:table>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:table xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:table>"
        },{
          caption: "Add <sp:referencePointer>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referencePointer xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:referencePointer>"
        },{
          caption: "Add <sp:mathExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:mathExpression xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:mathExpression>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:boldText": {
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:emphasizedText": {
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:quote": {
        oneliner: true,
        menu:[{
          caption: "Add <sp:source>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:source xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:source>"
        },{
          caption: "Add <sp:quoteContent>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:quoteContent xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:quoteContent>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:source": {
        hasText: true,
        oneliner: true,
        mustBeBefore: ["sp:quoteContent"],
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:quoteContent": {
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:figure": {
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:description>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:description xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:description>"
        },{
          caption: "Add <sp:image>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:image xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:image>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:description": {
        mustBeBefore: ["sp:image"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:image": {
        asker: Xonomy.askLongString,
        hasText: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:list": {
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:listItem>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:listItem xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:listItem>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:listItem": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:code": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:table": {
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:tableDescription>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:tableDescription xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:tableDescription>"
        },{
          caption: "Add <sp:tableRow>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:tableRow xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:tableRow>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:tableDescription": {
        mustBeBefore: ["sp:tableRow"],
        asker: Xonomy.askString,
        hasText: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:tableRow": {
        menu: [{
          caption: "Add <sp:tableCell>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:tableCell xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:tableCell>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:tableCell": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:referencePointer": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:mathExpression": {
        hasText: true,
        menu: [{
          caption: "Add <sp:sum>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:sum xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:sum>"
        },{
          caption: "Add <sp:limit>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:limit xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:limit>"
        },{
          caption: "Add <sp:integral>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:integral xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:integral>"
        },{
          caption: "Add <sp:anyExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:anyExpression xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:anyExpression>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:sum": {
        mustBeBefore: ["sp:limit", "sp:integral", "sp:anyExpression"],
        menu: [{
          caption: "Add <sp:counter>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:counter xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:counter>"
        },{
          caption: "Add <sp:begin>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:begin xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:begin>"
        },{
          caption: "Add <sp:end>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:end xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:end>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:content>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:limit": {
        mustBeBefore: ["sp:integral", "sp:anyExpression"],
        menu: [{
          caption: "Add <sp:variable>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:variable xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:variable>"
        },{
          caption: "Add <sp:target>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:target xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:target>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:content>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:integral": {
        mustBeBefore: ["sp:anyExpression"],
        menu: [{
          caption: "Add <sp:begin>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:begin xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:begin>"
        },{
          caption: "Add <sp:end>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:end xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:end>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:content>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:counter": {
        mustBeBefore: ["sp:begin", "sp:end", "sp:content"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:begin": {
        mustBeBefore: ["sp:end", "sp:content"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:end": {
        mustBeBefore: ["sp:content"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:variable": {
        mustBeBefore: ["sp:target", "sp:content"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:target": {
        mustBeBefore: ["sp:content"],
        hasText: true,
        oneliner: true,
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:content": {
        hasText: true,
        menu: [{
          caption: "Add <sp:sum>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:sum xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:sum>"
        },{
          caption: "Add <sp:limit>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:limit xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:limit>"
        },{
          caption: "Add <sp:integral>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:integral xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:integral>"
        },{
          caption: "Add <sp:anyExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:anyExpression xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:anyExpression>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:anyExpression": {
        asker: Xonomy.askString,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:chapter": {
        mustBeBefore: ["sp:references"],
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add attribute 'level'",
          action: Xonomy.newAttribute,
          actionParameter: {name: "level", value: 1},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("level");
          }
        },{
          caption: "Add <sp:title>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:title xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:title>"
        },{
          caption: "Add <sp:paragraph>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:paragraph xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:paragraph>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          },
          "level": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @level",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:references": {
        menu:[{
          caption: "Add <sp:reference>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:reference xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:reference>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:reference": {
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add attribute 'refPartId'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("refPartId");
          }
        },{
          caption: "Add RDF attribute 'href'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"href", value:XonomyApiService.scientificPublicationPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("href");
          }
        },{
          caption: "Add RDF attribute 'rel'",
          action: Xonomy.newAttribute,
          actionParameter: {name:"rel", value:XonomyApiService.metadataPrefix},
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("rel");
          }
        },{
          caption: "Add <sp:referenceAuthors>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceAuthors xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:referenceAuthors>"
        },{
          caption: "Add <sp:yearIssued>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:yearIssued xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:yearIssued>"
        },{
          caption: "Add <sp:referenceTitle>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceTitle xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:referenceTitle>"
        },{
          caption: "Add <sp:publisherName>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:publisherName xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:publisherName>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }],
        attributes: {
          "sp:id": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @sp:id",
              action: Xonomy.deleteAttribute
            }]
          },
          "refPartId": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @refPartId",
              action: Xonomy.deleteAttribute
            }]
          },
          "href": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @href",
              action: Xonomy.deleteAttribute
            }]
          },
          "rel": {
            asker: Xonomy.askString,
            menu: [{
              caption: "Delete this @rel",
              action: Xonomy.deleteAttribute
            }]
          }
        }
      },
      "sp:referenceAuthors": {
        mustBeBefore: ["sp:yearIssued", "sp:referenceTitle", "sp:publisherName"],
        menu:[{
          caption: "Add <sp:referenceAuthor>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceAuthor xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:referenceAuthor>"
        }, {
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:referenceAuthor": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:yearIssued": {
        mustBeBefore: ["sp:referenceTitle", "sp:publisherName"],
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:referenceTitle": {
        mustBeBefore: ["sp:publisherName"],
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      },
      "sp:publisherName": {
        asker: Xonomy.askString,
        hasText: true,
        oneliner: true,
        menu:[{
          caption: "Delete element",
          action: Xonomy.deleteElement
        }]
      }
    }
  }
}
