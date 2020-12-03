package org.kie.workbench.common.stunner.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class StunnerCommonImageResources_default_InlineClientBundleGenerator implements org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources {
  private static StunnerCommonImageResources_default_InlineClientBundleGenerator _instance0 = new StunnerCommonImageResources_default_InlineClientBundleGenerator();
  private void commonIconsSpriteInitializer() {
    commonIconsSprite = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "commonIconsSprite",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage),
      0, 0, 16, 58, false, false
    );
  }
  private static class commonIconsSpriteInitializer {
    static {
      _instance0.commonIconsSpriteInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return commonIconsSprite;
    }
  }
  public com.google.gwt.resources.client.ImageResource commonIconsSprite() {
    return commonIconsSpriteInitializer.get();
  }
  private void deleteInitializer() {
    delete = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "delete",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage0),
      0, 0, 16, 16, false, false
    );
  }
  private static class deleteInitializer {
    static {
      _instance0.deleteInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return delete;
    }
  }
  public com.google.gwt.resources.client.ImageResource delete() {
    return deleteInitializer.get();
  }
  private void drdInitializer() {
    drd = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "drd",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage1),
      0, 0, 16, 16, false, false
    );
  }
  private static class drdInitializer {
    static {
      _instance0.drdInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return drd;
    }
  }
  public com.google.gwt.resources.client.ImageResource drd() {
    return drdInitializer.get();
  }
  private void editInitializer() {
    edit = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "edit",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage2),
      0, 0, 16, 16, false, false
    );
  }
  private static class editInitializer {
    static {
      _instance0.editInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return edit;
    }
  }
  public com.google.gwt.resources.client.ImageResource edit() {
    return editInitializer.get();
  }
  private void formInitializer() {
    form = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "form",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage3),
      0, 0, 16, 16, false, false
    );
  }
  private static class formInitializer {
    static {
      _instance0.formInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return form;
    }
  }
  public com.google.gwt.resources.client.ImageResource form() {
    return formInitializer.get();
  }
  private void gearsInitializer() {
    gears = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "gears",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage4),
      0, 0, 16, 16, false, false
    );
  }
  private static class gearsInitializer {
    static {
      _instance0.gearsInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return gears;
    }
  }
  public com.google.gwt.resources.client.ImageResource gears() {
    return gearsInitializer.get();
  }
  private void commonIconsSpriteCssInitializer() {
    commonIconsSpriteCss = new org.kie.workbench.common.stunner.core.client.resources.StunnerCommonCssResource() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "commonIconsSpriteCss";
      }
      public String getText() {
        return (".GFVDQLFDBJ{background:" + ("url('" + StunnerCommonImageResources_default_InlineClientBundleGenerator.this.commonIconsSprite().getSafeUri().asString() + "')")  + ";width:" + ("16px")  + ";height:" + ("16px")  + ";}");
      }
      public java.lang.String commonIconsSpriteClass() {
        return "GFVDQLFDBJ";
      }
    }
    ;
  }
  private static class commonIconsSpriteCssInitializer {
    static {
      _instance0.commonIconsSpriteCssInitializer();
    }
    static org.kie.workbench.common.stunner.core.client.resources.StunnerCommonCssResource get() {
      return commonIconsSpriteCss;
    }
  }
  public org.kie.workbench.common.stunner.core.client.resources.StunnerCommonCssResource commonIconsSpriteCss() {
    return commonIconsSpriteCssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAA6CAYAAAC5xNkxAAAChElEQVR4Xu2Vy6tOURjGH7fIpXAYCGUmUaTkJCOS+y1FiJRLOkVR/gFGBjIRRjJTFDIwkKEoyS2S+6dMXAYMCLk9z/e+e3/rW2uvc3bfGTA4T/3aa73redfaa6/LBqo1kfyJUKy2JsCSCqmsWFZfyPuAD7Ckd47KioUe5ZSKX7cupVS5QM7VRN62DnrIONiHqoO8ymnTTaSvmEPeRCeQGnPIm2g3UmMOeRPNQ2rMIW+ioUiNOeSt1G+k5hh5stIOixNi5MnqCdKEGHmyuoE0IUaerC4jTYiRZ0D/UrP9OZqMDBvqaBZsGY+Tz16u1HSyhAyPG2AX6QtY8pmoDUv9+Qhm2Or1RWShx+55rMvrzZt5ELnogav+FB/RGm0/Oe0J88kB0iCH4XroRvGNfCK/gtgu96n83Z93PNbUFg8+IIPJMDIVrU4KXSdjyEayrQj+RGukBUXQdT5ouxS1ldIr1+ngStTWpk0w030yBPkpXINNYQOCKUh30Rqp6iNqFSSVf/hTOaX0v1NQIxRJ+hs/9/Ih2DJ+Jd3kIHlDjii5ULGRtBJK2uH1cCMVo473enMjxZpBlqH3rayr/Wx7U9+aCRtVh0nfKPy4tTXHn1qJUWHDgP4H7SUvydOayKucUg1yFHbKemM9Wete5ZRSj+vCQEaTyGKyCpZTSpXVZC7sNt7s6OrSmdgDG/0V2U5Wek4pVZbDblmduNvOLTIZdg50T+xzv9426aCvKUwjI2CndQ0qOtDHGQu7yqY4Gl23tP4FKuvCOQU77kkHmsIx8pa8dnQjqSMlaAon3d/RFFYE5Y46CFXZQbEKz8hjR9e85h6rsoN+vUED9bZyQbKVOzlMO5uZ/dVf30VGSjEK4sYAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAn0lEQVR4Xo2SwQnCQBQF38EGhFiKJXmxnvTgwYtFiC1YQ/TsVd1hdUn+y4cdGEiWeSGESOvsip8gZ90MqqM/XHOW8io+Zj5VR9NPrjmbN2wa8XV7bXBzLp46pV084Fjcqn6oHmnZLLjJXzGT1hjlYSatcZCHmbTGXh5m0hobeZhJu8pbHkdpUvjD4iBKk3KXD6I0KVf5IEqTcpEPojSNL0H4kun7LhEQAAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage1 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAsUlEQVR4XqWTPRIBQRCFW5E5Ixs6gVTidnI2lJG5AIX3GaNar9ndMq/qS3b791WP2XgtxF7cxFGsxeQrokeNuItHYOuD+rSzbjJcfFBJc0sjx+TBAlOxEifrJmZeK2AEhtDlasmojTi4wPb9jX/EEPsxkSqxcuZsaYoZgSWxR0wEurD/oKoLVK/gTeTKsokYlwthaNHEkuhKd6aIk2VGXeLfh+RVdcqoscrHhJb24zk/AblNcI8rKEHmAAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage2 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA0ElEQVR4Xp2Suw4BQRSGD1qVSqJWafAQKk/hKUSC8Qh0REgU+1YqpUSChsLlPzuXzM7Ozix/8iUz5/Jlslmi/zIGd7B0G2XSAy/wAVcu8CHGHlTAFAzAiKSEX5IO8OVSwAFUwULNPsAQdHmZw8WjvhREL2smdjMmEJRdnpF8kUlIICi/XANba6ZQICi/zEnAU53T+AQtkh/QXebcKCJogw3ogzOYWz1OVLBStR1oWnWdoKAOTmANOmYim6CgQVISSlBQJl5B6Fd2eZNH8CtG8AW1yHHatH9E6gAAAABJRU5ErkJggg==";
  private static final java.lang.String externalImage3 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA4UlEQVR4Xq3TOw4BURTG8RsNpVaQaEWnsQCN9yMSiUg0EtHYhE6hZS82IFGIRoF4FBah9D/mzM2EETJ8yS+Zmdxz7px5GPOnDHHE7kuyVmpsLpig9UETdV0rNTbSseG98CYx5FExTo2NnFSRRRcd1UYYA+PsfkIPZa2xkZMixlhjpZaII4MbRrpe7valwacRUoggjZrxaSAPJ4okEkp2DyGnxwvMUdAaG3eEKa44q4NxGkmBjDDT9YFGKHmOAzXwxreB+xb22KqNcWZ/jm+Dn+7gYr77lF0vn3KQn6n/qPw1d+YMT2RzN2xnAAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage4 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABNElEQVR4XpWTzStFURTFF5LymDA2lrxiRjJSYq4oL2VgrCj/AH+AqZHMlAFlYCB/gnxlxoAxAwaEfK31zjrvnnd7uFb92vvsffbZ93xc4H8asO0g7WmiiMrki6yTR/sN1UvGSVs+QT2Ra4TizVwOE7aXCBMqHo+RUcdOHev2WAuiiew6cGAr7pF1WyQbLhgiS+SGrMK68ETxQh7IRxJb8Dz5r7bHjlU16+A5aSatpAfZIlFHpJNMk7kYfEfWaSQGrZ0kt5fL1aRPLrLAfi5XpxmESWekBT9v4RBhC1NItiCdIOvU6BB1C5L8N1vV1LTtoDrEojtyZX8F4RqfyTBZJrdkTcVR8SHpJlQ073H6kGLXLo+rDymvPjKJ35/yJ9mqT/2tfoSu+pl0RunhFtagrW6iJOcbwjFj8NaxKTkAAAAASUVORK5CYII=";
  private static com.google.gwt.resources.client.ImageResource commonIconsSprite;
  private static com.google.gwt.resources.client.ImageResource delete;
  private static com.google.gwt.resources.client.ImageResource drd;
  private static com.google.gwt.resources.client.ImageResource edit;
  private static com.google.gwt.resources.client.ImageResource form;
  private static com.google.gwt.resources.client.ImageResource gears;
  private static org.kie.workbench.common.stunner.core.client.resources.StunnerCommonCssResource commonIconsSpriteCss;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      commonIconsSprite(), 
      delete(), 
      drd(), 
      edit(), 
      form(), 
      gears(), 
      commonIconsSpriteCss(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("commonIconsSprite", commonIconsSprite());
        resourceMap.put("delete", delete());
        resourceMap.put("drd", drd());
        resourceMap.put("edit", edit());
        resourceMap.put("form", form());
        resourceMap.put("gears", gears());
        resourceMap.put("commonIconsSpriteCss", commonIconsSpriteCss());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'commonIconsSprite': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::commonIconsSprite()();
      case 'delete': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::delete()();
      case 'drd': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::drd()();
      case 'edit': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::edit()();
      case 'form': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::form()();
      case 'gears': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::gears()();
      case 'commonIconsSpriteCss': return this.@org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources::commonIconsSpriteCss()();
    }
    return null;
  }-*/;
}
