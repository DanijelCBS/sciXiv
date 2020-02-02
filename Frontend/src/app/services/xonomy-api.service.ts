import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class XonomyApiService {
  static metadataPrefoix = "pred";
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
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("typeof");
          }
        }, {
          caption: "Add <sp:metadata>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:metadata></sp:metadata>"
        }, {
          caption: "Add <sp:abstract>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:abstract></sp:abstract>"
        }, {
          caption: "Add <sp:chapter>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:chapter></sp:chapter>"
        }, {
          caption: "Add <sp:references>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:references></sp:references>"
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
        menu: [{
          caption: "Add <sp:title>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:title></sp:title>"
        }, {
          caption: "Add <sp:authors>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:authors></sp:authors>"
        }, {
          caption: "Add <sp:keywords>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:keywords></sp:keywords>"
        }]
      },
      "sp:title": {
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
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
            actionParameter: "<sp:author></sp:author>"
          }]
        },
        "sp:author": {
          menu: [{
            caption: "Add RDF attribute 'href'",
            action: Xonomy.newAttribute,
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("href");
            }
          }, {
            caption: "Add RDF attribute 'rel'",
            action: Xonomy.newAttribute,
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("rel");
            }
          }, {
            caption: "Add RDF attribute 'typeof'",
            action: Xonomy.newAttribute,
            hideIf: function (jsElement) {
              return jsElement.hasAttribute("typeof");
            }
          }, {
            caption: "Add <sp:name>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:name></sp:name>"
          }, {
            caption: "Add <sp:affiliation>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:affiliation></sp:affiliation>"
          }, {
            caption: "Add <sp:city>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:city></sp:city>"
          }, {
            caption: "Add <sp:state>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:state></sp:state>"
          }, {
            caption: "Add <sp:email>",
            action: Xonomy.newElementChild,
            actionParameter: "<sp:email></sp:email>"
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
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'about'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("about");
          }
        },{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
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
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'about'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("about");
          }
        },{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
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
        asker: Xonomy.askString
      },
      "sp:state": {
        asker: Xonomy.askString
      },
      "sp:email": {
        asker: Xonomy.askString
      },
      "sp:keywords": {
        menu: [{
          caption: "Add <sp:keyword>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:keyword></sp:keyword>"
        }]
      },
      "sp:keyword": {
        asker: Xonomy.askString,
        menu: [{
          caption: "Add RDF attribute 'property'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("property");
          }
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
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
        },{
          caption: "Add <sp:paragraph>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:paragraph></sp:paragraph>"
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
          actionParameter: "<sp:boldText></sp:boldText>"
        },{
          caption: "Add <sp:emphasizedText>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:emphasizedText></sp:emphasizedText>"
        },{
          caption: "Add <sp:quote>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:quote></sp:quote>"
        },{
          caption: "Add <sp:figure>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:figure></sp:figure>"
        },{
          caption: "Add <sp:list>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:list></sp:list>"
        },{
          caption: "Add <sp:code>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:code></sp:code>"
        },{
          caption: "Add <sp:table>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:table></sp:table>"
        },{
          caption: "Add <sp:referencePointer>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referencePointer></sp:referencePointer>"
        },{
          caption: "Add <sp:mathExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:mathExpression></sp:mathExpression>"
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
        asker: Xonomy.askString
      },
      "sp:emphasizedText": {
        hasText: true,
        asker: Xonomy.askString
      },
      "sp:quote": {
        oneliner: true,
        menu:[{
          caption: "Add <sp:source>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:source></sp:source>"
        },{
          caption: "Add <sp:quoteContent>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:quoteContent></sp:quoteContent>"
        }]
      },
      "sp:source": {
        asker: Xonomy.askString
      },
      "sp:quoteContent": {
        asker: Xonomy.askString
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
          actionParameter: "<sp:description></sp:description>"
        },{
          caption: "Add <sp:image>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:image></sp:image>"
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
        asker: Xonomy.askString
      },
      "sp:image": {
        asker: Xonomy.askString
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
          actionParameter: "<sp:listItem></sp:listItem>"
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
        asker: Xonomy.askString
      },
      "sp:code": {
        asker: Xonomy.askString
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
          actionParameter: "<sp:tableDescription></sp:tableDescription>"
        },{
          caption: "Add <sp:tableRow>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:tableRow></sp:tableRow>"
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
        asker: Xonomy.askString
      },
      "sp:tableRow": {
        menu: [{
          caption: "Add <sp:tableCell>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:tableCell></sp:tableCell>"
        }]
      },
      "sp:tableCell": {
        asker: Xonomy.askString
      },
      "sp:referencePointer": {
        asker: Xonomy.askString,
        menu: [{
          caption: "Add attribute 'sp:id'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("sp:id");
          }
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
          actionParameter: "<sp:sum></sp:sum>"
        },{
          caption: "Add <sp:limit>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:limit></sp:limit>"
        },{
          caption: "Add <sp:integral>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:integral></sp:integral>"
        },{
          caption: "Add <sp:anyExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:anyExpression></sp:anyExpression>"
        }]
      },
      "sp:sum": {
        menu: [{
          caption: "Add <sp:counter>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:counter></sp:counter>"
        },{
          caption: "Add <sp:begin>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:begin></sp:begin>"
        },{
          caption: "Add <sp:end>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:end></sp:end>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content></sp:content>"
        }]
      },
      "sp:limit": {
        menu: [{
          caption: "Add <sp:variable>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:variable></sp:variable>"
        },{
          caption: "Add <sp:target>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:target></sp:target>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content></sp:content>"
        }]
      },
      "sp:integral": {
        menu: [{
          caption: "Add <sp:begin>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:begin></sp:begin>"
        },{
          caption: "Add <sp:end>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:end></sp:end>"
        },{
          caption: "Add <sp:content>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:content></sp:content>"
        }]
      },
      "sp:counter": {
        asker: Xonomy.askString
      },
      "sp:begin": {
        asker: Xonomy.askString
      },
      "sp:end": {
        asker: Xonomy.askString
      },
      "sp:variable": {
        asker: Xonomy.askString
      },
      "sp:target": {
        asker: Xonomy.askString
      },
      "sp:content": {
        hasText: true,
        menu: [{
          caption: "Add <sp:sum>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:sum></sp:sum>"
        },{
          caption: "Add <sp:limit>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:limit></sp:limit>"
        },{
          caption: "Add <sp:integral>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:integral></sp:integral>"
        },{
          caption: "Add <sp:anyExpression>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:anyExpression></sp:anyExpression>"
        }]
      },
      "sp:anyExpression": {
        asker: Xonomy.askString
      },
      "sp:chapter": {
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
          actionParameter: "<sp:title></sp:title>"
        },{
          caption: "Add <sp:paragraph>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:paragraph></sp:paragraph>"
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
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("href");
          }
        },{
          caption: "Add RDF attribute 'rel'",
          action: Xonomy.newAttribute,
          hideIf: function (jsElement) {
            return jsElement.hasAttribute("rel");
          }
        },{
          caption: "Add <sp:referenceAuthors>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceAuthors></sp:referenceAuthors>"
        },{
          caption: "Add <sp:yearIssued>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:yearIssued></sp:yearIssued>"
        },{
          caption: "Add <sp:referenceTitle>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceTitle></sp:referenceTitle>"
        },{
          caption: "Add <sp:publisherName>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:publisherName></sp:publisherName>"
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
        menu:[{
          caption: "Add <sp:referenceAuthor>",
          action: Xonomy.newElementChild,
          actionParameter: "<sp:referenceAuthor></sp:referenceAuthor>"
        }]
      },
      "sp:referenceAuthor": {
        asker: Xonomy.askString
      },
      "sp:yearIssued": {
        asker: Xonomy.askString
      },
      "sp:referenceTitle": {
        asker: Xonomy.askString
      },
      "sp:publisherName": {
        asker: Xonomy.askString
      }
    }
  }
}
