package org.codehaus.groovy.grails.web.taglib

import grails.util.GrailsUtil
import grails.util.MockRequestDataValueProcessor;

import javax.servlet.http.Cookie

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.codehaus.groovy.grails.web.pages.GroovyPageBinding
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.servlet.support.RequestDataValueProcessor
import org.springframework.mock.web.MockHttpServletResponse
import org.codehaus.groovy.grails.support.MockApplicationContext

class ApplicationTagLibTests extends AbstractGrailsTagTests {

    void registerRequestDataValueProcessor() {
        RequestDataValueProcessor requestDataValueProcessor = new MockRequestDataValueProcessor();
        MockApplicationContext applicationContext = (MockApplicationContext)ctx;
        applicationContext.registerMockBean("requestDataValueProcessor",requestDataValueProcessor);
    }
    void unRegisterRequestDataValueProcessor() {
        MockApplicationContext applicationContext = (MockApplicationContext)ctx;
        applicationContext.registerMockBean("requestDataValueProcessor",null);
    }

    void testResourceTagWithPluginAttribute() {
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", plugin:"controllers")}'
        assertOutputEquals "/test/plugins/controllers-${GrailsUtil.getGrailsVersion()}/images/foo.jpg", template
    }

    void testResourceTagWithPluginAttributeAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", plugin:"controllers")}'
        assertOutputEquals "/test/plugins/controllers-${GrailsUtil.getGrailsVersion()}/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    void testResourceTagWithImplicitPlugin() {
        def template = '${resource(file:"images/foo.jpg")}'

        request.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE, new GroovyPageBinding("/plugin/one"))
        assertOutputEquals "/plugin/one/images/foo.jpg", template
    }

    void testResourceTagWithImplicitPluginAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        
        def template = '${resource(file:"images/foo.jpg")}'

        request.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE, new GroovyPageBinding("/plugin/one"))
        assertOutputEquals "/plugin/one/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor();
    }

    void testResourceTagWithContextPathAttribute() {
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", contextPath:"/foo")}'
        assertOutputEquals "/foo/images/foo.jpg", template

        request.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE, new GroovyPageBinding("/plugin/one"))
        assertOutputEquals "/foo/images/foo.jpg", template
    }

    void testResourceTagWithContextPathAttributeAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", contextPath:"/foo")}'
        assertOutputEquals "/foo/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template

        request.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE, new GroovyPageBinding("/plugin/one"))
        assertOutputEquals "/foo/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    void testResourceTagWithPluginAttributeAndNone() {
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", plugin:"none")}'
        assertOutputEquals "/test/images/foo.jpg", template
    }

    void testResourceTagWithPluginAttributeAndNoneAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg", plugin:"none")}'
        assertOutputEquals "/test/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    void testResourceTag() {
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg")}'
        assertOutputEquals '/test/images/foo.jpg', template

        template = '${resource(dir:"images",file:"foo.jpg")}'
        assertOutputEquals '/test/images/foo.jpg', template
    }

    void testResourceTagAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        request.contextPath = '/test'
        def template = '${resource(file:"images/foo.jpg")}'
        assertOutputEquals '/test/images/foo.jpg?requestDataValueProcessorParamName=paramValue', template

        template = '${resource(dir:"images",file:"foo.jpg")}'
        assertOutputEquals '/test/images/foo.jpg?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor()
    }


    void testResourceTagDirOnly() {
        request.contextPath = '/test'
        def template = '${resource(dir:"jquery")}'
        assertOutputEquals '/test/jquery', template
    }

    void testResourceTagDirOnlyAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        request.contextPath = '/test'
        def template = '${resource(dir:"jquery")}'
        assertOutputEquals '/test/jquery?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor()
    }

    void testUseJessionIdWithCreateLink() {
        def response = new JsessionIdMockHttpServletResponse()
        ApplicationTagLib.metaClass.getResponse = {-> response}
        def tagLibBean = appCtx.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
        ga.config.grails.views.enable.jsessionid=true
        tagLibBean.afterPropertiesSet()
        assertTrue(tagLibBean.@useJsessionId)

        def template = '<g:createLink controller="foo" action="test" />'
        assertOutputEquals "/foo/test;jsessionid=test", template

        ga.config.grails.views.enable.jsessionid=false
        tagLibBean.afterPropertiesSet()
        assertFalse(tagLibBean.@useJsessionId)
        assertOutputEquals "/foo/test", template
    }

    void testUseJessionIdWithCreateLinkAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        def response = new JsessionIdMockHttpServletResponse()
        ApplicationTagLib.metaClass.getResponse = {-> response}
        def tagLibBean = appCtx.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
        ga.config.grails.views.enable.jsessionid=true
        tagLibBean.afterPropertiesSet()
        assertTrue(tagLibBean.@useJsessionId)

        def template = '<g:createLink controller="foo" action="test" />'
        assertOutputEquals "/foo/test?requestDataValueProcessorParamName=paramValue;jsessionid=test", template

        ga.config.grails.views.enable.jsessionid=false
        tagLibBean.afterPropertiesSet()
        assertFalse(tagLibBean.@useJsessionId)
        assertOutputEquals "/foo/test?requestDataValueProcessorParamName=paramValue", template
    }

    void testObtainCookieValue() {
        def cookie = new Cookie("foo", "bar")
        request.cookies = [cookie] as Cookie[]

        def template = '<g:cookie name="foo" />'
        assertOutputEquals "bar", template

        template = '${cookie(name:"foo")}'
        assertOutputEquals "bar", template
    }

    void testObtainHeaderValue() {
        request.addHeader "FOO", "BAR"
        def template = '<g:header name="FOO" />'
        assertOutputEquals "BAR", template

        template = '${header(name:"FOO")}'
        assertOutputEquals "BAR", template
    }

    void testClonedUrlFromVariable() {
        def template = '''<g:set var="urlMap" value="${[controller: 'test', action: 'justdoit']}"/>${urlMap.controller},${urlMap.action}<g:link url="${urlMap}">test</g:link>${urlMap.controller},${urlMap.action}'''
        assertOutputEquals('test,justdoit<a href="/test/justdoit">test</a>test,justdoit', template)
    }

    void testLinkWithMultipleParameters() {
        def template = '<g:link controller="foo" action="action" params="[test: \'test\', test2: \'test2\']">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2">test</a>', template)
    }

    void testLinkWithMultipleParametersAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        def template = '<g:link controller="foo" action="action" params="[test: \'test\', test2: \'test2\']">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        this.unRegisterRequestDataValueProcessor()
        
    }

    void testLinkWithMultipleCollectionParameters() {
        def template = '<g:link controller="foo" action="action" params="[test: [\'test-a\',\'test-b\'], test2: [\'test2-a\',\'test2-b\'] as String[]]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;test=test-b&amp;test2=test2-a&amp;test2=test2-b">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;test=test-b&amp;test2=test2-a&amp;test2=test2-b">test</a>', template)
    }

    void testLinkWithMultipleCollectionParametersAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        def template = '<g:link controller="foo" action="action" params="[test: [\'test-a\',\'test-b\'], test2: [\'test2-a\',\'test2-b\'] as String[]]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;test=test-b&amp;test2=test2-a&amp;test2=test2-b&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;test=test-b&amp;test2=test2-a&amp;test2=test2-b&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testLinkWithCollectionParameter() {
        def template = '<g:link controller="foo" action="action" params="[test: [\'test-a\']]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test-a">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test-a">test</a>', template)
    }

    void testLinkWithCollectionParameterAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        def template = '<g:link controller="foo" action="action" params="[test: [\'test-a\']]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?test=test-a&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testLinkWithCharCollectionParameter() {
        def template = '<g:link controller="foo" action="action" params="[letter: [\'a\' as char]]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?letter=a">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?letter=a">test</a>', template)
    }
    void testLinkWithCharCollectionParameterAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        def template = '<g:link controller="foo" action="action" params="[letter: [\'a\' as char]]">test</g:link>'
        assertOutputEquals('<a href="/foo/action?letter=a&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        // test caching too
        assertOutputEquals('<a href="/foo/action?letter=a&amp;requestDataValueProcessorParamName=paramValue">test</a>', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkWithCollectionParameter() {
        def template = '<% l=\'a\' %>${g.createLink(controller:"foo", action:"action", params:[letter:[l]])} ${g.createLink(controller:"foo", action:"action", params:[letter:[l]])}'
        assertOutputEquals('/foo/action?letter=a /foo/action?letter=a', template)
        // test caching too
        assertOutputEquals('/foo/action?letter=a /foo/action?letter=a', template)
    }

    void testCreateLinkWithCollectionParameterAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        def template = '<% l=\'a\' %>${g.createLink(controller:"foo", action:"action", params:[letter:[l]])} ${g.createLink(controller:"foo", action:"action", params:[letter:[l]])}'
        assertOutputEquals('/foo/action?letter=a&requestDataValueProcessorParamName=paramValue /foo/action?letter=a&requestDataValueProcessorParamName=paramValue', template)
        // test caching too
        assertOutputEquals('/foo/action?letter=a&requestDataValueProcessorParamName=paramValue /foo/action?letter=a&requestDataValueProcessorParamName=paramValue', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testLikeWithElementId() {
        def template = '<g:link elementId="myId" controller="foo" action="list">bar</g:link>'
        assertOutputEquals('<a href="/foo/list" id="myId">bar</a>', template)
    }

    void testLikeWithElementIdAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        def template = '<g:link elementId="myId" controller="foo" action="list">bar</g:link>'
        assertOutputEquals('<a href="/foo/list?requestDataValueProcessorParamName=paramValue" id="myId">bar</a>', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testLinkWithMultipleParametersAndElementId() {
        def template = '<g:link elementId="myid" controller="foo" action="action" params="[test: \'test\', test2: \'test2\']">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2" id="myid">test</a>', template)
    }

    void testLinkWithMultipleParametersAndElementIdAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        def template = '<g:link elementId="myid" controller="foo" action="action" params="[test: \'test\', test2: \'test2\']">test</g:link>'
        assertOutputEquals('<a href="/foo/action?test=test&amp;test2=test2&amp;requestDataValueProcessorParamName=paramValue" id="myid">test</a>', template)
        this.unRegisterRequestDataValueProcessor()
    }

    void testLinkWithFragment() {
        def template = '<g:link controller="foo" action="bar" fragment="test">link</g:link>'
        profile("link rendering") {
            assertOutputEquals('<a href="/foo/bar#test">link</a>', template)
        }
    }

   void testLinkWithFragmentAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        
        def template = '<g:link controller="foo" action="bar" fragment="test">link</g:link>'
        profile("link rendering") {
            assertOutputEquals('<a href="/foo/bar?requestDataValueProcessorParamName=paramValue#test">link</a>', template)
        }
        this.unRegisterRequestDataValueProcessor()
    }



    void testCreateLinkWithFlowExecutionKeyAndEvent() {
        request.flowExecutionKey = '12345'

        def template = '<g:createLink controller="foo" action="bar" event="boo" />'
        assertOutputEquals('/foo/bar?execution=12345&_eventId=boo', template)
    }

    void testCreateLinkWithFlowExecutionKeyAndEventAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        
        request.flowExecutionKey = '12345'

        def template = '<g:createLink controller="foo" action="bar" event="boo" />'
        assertOutputEquals('/foo/bar?execution=12345&_eventId=boo&requestDataValueProcessorParamName=paramValue', template)

        this.unRegisterRequestDataValueProcessor()
    }

    void testLinkWithFlowExecutionKeyAndEvent() {
        request.flowExecutionKey = '12345'

        def template = '<g:link controller="foo" action="bar" event="boo" >link</g:link>'
        assertOutputEquals('<a href="/foo/bar?execution=12345&amp;_eventId=boo">link</a>', template)
    }

    void testLinkWithFlowExecutionKeyAndEventAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        
        request.flowExecutionKey = '12345'

        def template = '<g:link controller="foo" action="bar" event="boo" >link</g:link>'
        assertOutputEquals('<a href="/foo/bar?execution=12345&amp;_eventId=boo&amp;requestDataValueProcessorParamName=paramValue">link</a>', template)
        this.unRegisterRequestDataValueProcessor()   
    }

    void testSetTag() {
        def template = '<g:set var="one" value="two" />one: ${one}'
        assertOutputEquals('one: two', template)
    }

    void testSetTagWithBody() {
        def template = '<g:set var="one">two</g:set>one: ${one}'
        assertOutputEquals('one: two', template)
    }

    void testSetTagWithMap() {
        def template = '<g:set var="e" value="${c.a}"/>${e?.b}'
        assertOutputEquals('', template, [c:[:]])
        assertOutputEquals('foo', template, [c:[a:[b:'foo']]])
    }

    void testSetTagWithScope() {
        def template = '<g:set var="var1" value="1"/>${var1}<g:set var="var1" value="2"/> ${var1}<g:set var="var2" value="3" scope="request"/> ${var2}<g:set var="var2" value="4" scope="request"/> ${var2}'
        assertOutputEquals('1 2 3 4', template)
    }


    void testSetTagWithBeanName() {
        def template = '<g:set bean="grailsApplication" var="myVar"/>${myVar.initialised}'
        assertOutputEquals('true', template)

        template = '<g:set bean="grailsApplication" var="myRequestVar" scope="request"/>${request.myRequestVar.initialised}'
        assertOutputEquals('true', template)
    }

    void testSetTagWithBeanType() {
        def template = '<%@ page import="org.codehaus.groovy.grails.commons.*" %><g:set bean="${GrailsApplication}" var="myRequestVar" scope="request"/>${request.myRequestVar.initialised}'
        assertOutputEquals('true', template)
    }

    void testIteration() {
        def template = '''<g:set var="counter" value="${1}" />
<g:each in="${[10,11,12]}" var="myVal"><g:set var="counter" value="${myVal+counter}" />${counter}</g:each>'''

        printCompiledSource template
        assertOutputEquals('112234', template, [:], { it.toString().trim() })
    }

    void testMetaTag() {
        def template = '<g:meta name="app.version"/>'
        assertOutputEquals('0.9.9.1', template)
    }

    void testCreateLinkToWithDirAndLeadingSlash() {
        request.contextPath = "/"
        def template = '<g:createLinkTo dir="/images" file="foo.jpg" />'
        assertOutputEquals "/images/foo.jpg", template
    }

    void testCreateLinkToWithDirAndLeadingSlashAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();
        
        request.contextPath = "/"
        def template = '<g:createLinkTo dir="/images" file="foo.jpg" />'
        assertOutputEquals "/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkToWithDirAndLeadingNoLeadingSlash() {
        def template = '<g:createLinkTo dir="images" file="foo.jpg" />'
        assertOutputEquals "/images/foo.jpg", template
    }

    void testCreateLinkToWithDirAndLeadingNoLeadingSlashAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:createLinkTo dir="images" file="foo.jpg" />'
        assertOutputEquals "/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    
    void testCreateLinkToWithFileAndLeadingSlash() {
        def template = '<g:createLinkTo dir="/images" file="/foo.jpg" />'
        assertOutputEquals "/images/foo.jpg", template
    }

    void testCreateLinkToWithFileAndLeadingSlashAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:createLinkTo dir="/images" file="/foo.jpg" />'
        assertOutputEquals "/images/foo.jpg?requestDataValueProcessorParamName=paramValue", template
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkTo() {

        def template = '<g:resource dir="test" />'
        assertOutputEquals '/test', template

        template = '<g:resource dir="test" file="file" />'
        assertOutputEquals '/test/file', template

        template = '<g:resource dir="" />'
        assertOutputEquals '', template
    }

    void testCreateLinkToWithRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:resource dir="test" />'
        assertOutputEquals '/test?requestDataValueProcessorParamName=paramValue', template

        template = '<g:resource dir="test" file="file" />'
        assertOutputEquals '/test/file?requestDataValueProcessorParamName=paramValue', template

        template = '<g:resource dir="" />'
        assertOutputEquals '', template
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkToFilesInRoot() {
        def template = '<g:resource dir="/" file="test.gsp" />'
        assertOutputEquals '/test.gsp', template
    }

    void testCreateLinkToFilesInRootWithRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:resource dir="/" file="test.gsp" />'
        assertOutputEquals '/test.gsp?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor()
    }

    void testResourceFilesInRootWithContext() {
        def template = '<g:resource dir="/" file="test.gsp" />'
        request.contextPath = "/foo"
        assertOutputEquals '/foo/test.gsp', template
    }

    void testResourceFilesInRootWithContextAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:resource dir="/" file="test.gsp" />'
        request.contextPath = "/foo"
        assertOutputEquals '/foo/test.gsp?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkWithZeroId() {
        def template = '<g:createLink action="testAction" controller="testController" id="${id}"  />'
        assertOutputEquals '/testController/testAction/0', template, [id:0]
    }


    void testCreateLinkWithZeroIdAndRequestDataValueProcessor() {
        registerRequestDataValueProcessor();

        def template = '<g:createLink action="testAction" controller="testController" id="${id}"  />'
        assertOutputEquals '/testController/testAction/0?requestDataValueProcessorParamName=paramValue', template, [id:0] 
        this.unRegisterRequestDataValueProcessor()
    }

    void testCreateLinkURLEncoding() {
        def template = '<g:createLink action="testAction" controller="testController" params="[name:\'Marc Palmer\']"  />'
        assertOutputEquals '/testController/testAction?name=Marc+Palmer', template
    }

    void testCreateLinkURLEncodingWithRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        def template = '<g:createLink action="testAction" controller="testController" params="[name:\'Marc Palmer\']"  />'
        assertOutputEquals '/testController/testAction?name=Marc+Palmer&requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkURLEncodingWithHTMLChars() {
        def template = '<g:createLink action="testAction" controller="testController" params="[email:email]" />'
        assertOutputEquals '/testController/testAction?email=%3Cmarc%40anyware.co.uk%3E', template, [email:'<marc@anyware.co.uk>']
    }

    void testCreateLinkURLEncodingWithHTMLCharsAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        def template = '<g:createLink action="testAction" controller="testController" params="[email:email]" />'
        assertOutputEquals '/testController/testAction?email=%3Cmarc%40anyware.co.uk%3E&requestDataValueProcessorParamName=paramValue', template, [email:'<marc@anyware.co.uk>']
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkWithBase() {
        def template = '<g:createLink base="http://www128.myhost.com:3495" action="testAction" controller="testController" />'
        assertOutputEquals 'http://www128.myhost.com:3495/testController/testAction', template
    }

    void testCreateLinkWithBaseAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        def template = '<g:createLink base="http://www128.myhost.com:3495" action="testAction" controller="testController" />'
        assertOutputEquals 'http://www128.myhost.com:3495/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkWithUseJSessionIdAndContextPapth() {
        request.contextPath = "/foo"
        def taglib = appCtx.getBean(ApplicationTagLib.name)

        taglib.useJsessionId = true
        def template = '<g:createLink action="testAction" controller="testController" />'
        assertOutputEquals '/foo/testController/testAction', template
    }

    void testCreateLinkWithUseJSessionIdAndContextPapthAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        request.contextPath = "/foo"
        def taglib = appCtx.getBean(ApplicationTagLib.name)

        taglib.useJsessionId = true
        def template = '<g:createLink action="testAction" controller="testController" />'
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkWithContextPath() {
        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" />'
        assertOutputEquals '/foo/testController/testAction', template
    }

    void testCreateLinkWithContextPathAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" />'
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();

    }

    void testAbsoluteWithContextPath() {
        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" absolute="true" />'

        def linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals 'http://www.mysite.com/testController/testAction', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${true}" />'

        linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals 'http://www.mysite.com/testController/testAction', template
    }

    void testAbsoluteWithContextPathAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" absolute="true" />'

        def linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals 'http://www.mysite.com/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${true}" />'

        linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals 'http://www.mysite.com/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        this.unRegisterRequestDataValueProcessor();
    }

    void testAbsoluteFalseWithContextPath() {
        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" absolute="false" />'

        def linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals '/foo/testController/testAction', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${false}" />'

        linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals '/foo/testController/testAction', template
    }

    void testAbsoluteFalseWithContextPathAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        request.contextPath = "/foo"
        def template = '<g:createLink action="testAction" controller="testController" absolute="false" />'

        def linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${false}" />'

        linkGenerator = appCtx.getBean("grailsLinkGenerator")
        linkGenerator.configuredServerBaseURL="http://www.mysite.com"
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        this.unRegisterRequestDataValueProcessor();
    }

    void testAbsoluteWithContextPathAndNullConfig() {
        ConfigurationHolder.config = null
        request.contextPath = "/foo"
        request.serverPort = 8080
        def template = '<g:createLink action="testAction" controller="testController" absolute="true" />'
        assertOutputEquals 'http://localhost:8080/foo/testController/testAction', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${true}" />'
        assertOutputEquals 'http://localhost:8080/foo/testController/testAction', template
    }

    void testAbsoluteWithContextPathAndNullConfigAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        ConfigurationHolder.config = null
        request.contextPath = "/foo"
        request.serverPort = 8080
        def template = '<g:createLink action="testAction" controller="testController" absolute="true" />'
        assertOutputEquals 'http://localhost:8080/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${true}" />'
        assertOutputEquals 'http://localhost:8080/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        this.unRegisterRequestDataValueProcessor();
    }

    void testAbsoluteFalseWithContextPathAndNullConfig() {
        ConfigurationHolder.config = null
        request.contextPath = "/foo"
        request.serverPort = 8080
        def template = '<g:createLink action="testAction" controller="testController" absolute="false" />'
        assertOutputEquals '/foo/testController/testAction', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${false}" />'
        assertOutputEquals '/foo/testController/testAction', template
    }

    void testAbsoluteFalseWithContextPathAndNullConfigAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        ConfigurationHolder.config = null
        request.contextPath = "/foo"
        request.serverPort = 8080
        def template = '<g:createLink action="testAction" controller="testController" absolute="false" />'
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink action="testAction" controller="testController" absolute="${false}" />'
        assertOutputEquals '/foo/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        this.unRegisterRequestDataValueProcessor();
    }

    /**
     * Tests regression of <a href="http://jira.codehaus.org/browse/GRAILS-3368">GRAILS-3368</a>.
     * The context path should not be included in the generated link
     * if "base" is set to an empty string.
     */
    void testCreateLinkWithNoContextPath() {
        def template = '<g:createLink base="" action="testAction" controller="testController" />'
        assertOutputEquals '/testController/testAction', template
    }

    void testCreateLinkWithNoContextPathAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();
        def template = '<g:createLink base="" action="testAction" controller="testController" />'
        assertOutputEquals '/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkWithAbsolute() {
        def template = '<g:createLink absolute="true" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals 'http://localhost:8080/testController/testAction', template

        template = '<g:createLink absolute="${true}" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals 'http://localhost:8080/testController/testAction', template
    }

    void testCreateLinkWithAbsoluteAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        def template = '<g:createLink absolute="true" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals 'http://localhost:8080/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink absolute="${true}" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals 'http://localhost:8080/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testCreateLinkWithAbsoluteFalse() {
        def template = '<g:createLink absolute="false" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals '/testController/testAction', template

        template = '<g:createLink absolute="${false}" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals '/testController/testAction', template
    }

    void testCreateLinkWithAbsoluteFalseAndRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        def template = '<g:createLink absolute="false" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals '/testController/testAction?requestDataValueProcessorParamName=paramValue', template

        template = '<g:createLink absolute="${false}" action="testAction" controller="testController" />'
        request.serverPort = 8080
        assertOutputEquals '/testController/testAction?requestDataValueProcessorParamName=paramValue', template
        this.unRegisterRequestDataValueProcessor()
    }

    void testJoinStrings() {
        def template = /<g:join in="['Bruce', 'Adrian', 'Dave', 'Nicko', 'Steve']" delimiter="_"\/>/
        assertOutputEquals 'Bruce_Adrian_Dave_Nicko_Steve', template
    }

    void testJoinStringsWithEmptyDelimiter() {
        def template = /<g:join in="['Bruce', 'Adrian', 'Dave', 'Nicko', 'Steve']" delimiter=""\/>/
        assertOutputEquals 'BruceAdrianDaveNickoSteve', template
    }

    void testJoinWithEmptyCollection() {
        def template = /<g:join in="[]" delimiter="_"\/>/
        assertOutputEquals '', template
    }

    void testJoinWithoutSpecifyingIn() {
        def template = '<g:join delimiter="_"/>'
        def msg = shouldFail(GrailsTagException) {
            applyTemplate template
        }
        assertTrue msg?.startsWith('Tag ["join"] missing required attribute ["in"]')
    }

    void testJoinWithoutSpecifyingDelimiter() {
        def template = /<g:join in="['Bruce', 'Adrian', 'Dave', 'Nicko', 'Steve']"\/>/
        assertOutputEquals 'Bruce, Adrian, Dave, Nicko, Steve', template
    }

    void testImg() {
        def template = '<g:img dir="images" file="logo.png" width="100" height="200"/>'
        assertOutputEquals '<img src="/images/logo.png" width="100" height="200" />', template

        template = '<g:img file="logo.png" width="100" height="200"/>'
        assertOutputEquals '<img src="/images/logo.png" width="100" height="200" />', template
    }

    void testImgWithRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor()
        def template = '<g:img dir="images" file="logo.png" width="100" height="200"/>'
        assertOutputEquals '<img src="/images/logo.png?requestDataValueProcessorParamName=paramValue" width="100" height="200" />', template

        template = '<g:img file="logo.png" width="100" height="200"/>'
        assertOutputEquals '<img src="/images/logo.png?requestDataValueProcessorParamName=paramValue" width="100" height="200" />', template
        this.unRegisterRequestDataValueProcessor();
    }

    void testExternal() {
        def template = '<g:external uri="/js/main.js"/>'
        assertOutputEquals '<script src="/js/main.js" type="text/javascript"></script>\r\n', template

        template = '<g:external uri="/css/style.css"/>'
        assertOutputEquals '<link href="/css/style.css" type="text/css" rel="stylesheet" media="screen, projector"/>\r\n', template

        template = '<g:external uri="/css/print.css" media="print"/>'
        assertOutputEquals '<link href="/css/print.css" type="text/css" rel="stylesheet" media="print"/>\r\n', template

        template = '<g:external uri="/images/icons/iphone-icon.png" type="appleicon"/>'
        assertOutputEquals '<link href="/images/icons/iphone-icon.png" rel="apple-touch-icon"/>\r\n', template
    }

    void testExternalWithRequestDataValueProcessor() {
        this.registerRequestDataValueProcessor();

        def template = '<g:external uri="/js/main.js"/>'
        assertOutputEquals '<script src="/js/main.js?requestDataValueProcessorParamName=paramValue" type="text/javascript"></script>\r\n', template

        template = '<g:external uri="/css/style.css"/>'
        assertOutputEquals '<link href="/css/style.css?requestDataValueProcessorParamName=paramValue" type="text/css" rel="stylesheet" media="screen, projector"/>\r\n', template

        template = '<g:external uri="/css/print.css" media="print"/>'
        assertOutputEquals '<link href="/css/print.css?requestDataValueProcessorParamName=paramValue" type="text/css" rel="stylesheet" media="print"/>\r\n', template

        template = '<g:external uri="/images/icons/iphone-icon.png" type="appleicon"/>'
        assertOutputEquals '<link href="/images/icons/iphone-icon.png?requestDataValueProcessorParamName=paramValue" rel="apple-touch-icon"/>\r\n', template

        this.unRegisterRequestDataValueProcessor()
    }
}
