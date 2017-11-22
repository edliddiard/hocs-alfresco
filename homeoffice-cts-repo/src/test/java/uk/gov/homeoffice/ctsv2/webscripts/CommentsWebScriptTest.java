package uk.gov.homeoffice.ctsv2.webscripts;

import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.junit.Test;

/**
 * CommentsWebScript Sanitize comment post content Tests.
 * Created by dawud rahman on 28/04/2016.
 */

public class CommentsWebScriptTest extends BaseWebScriptTest {

    protected static CommentsWebScript commentsWebScript;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        commentsWebScript = new CommentsWebScript();
    }

    @Test
    public void testWiring() {
        assertNotNull(commentsWebScript);
    }

    @Test
    public void testSanitizeRemovesScripts() {
        String input =
                "<p>The quick brown fox jumps over the lazy dog</p>"
                        + "<script language=\"text/javascript\">alert(\"bad\");</script>";
        String expected = "The quick brown fox jumps over the lazy dog";
        String sanitized = commentsWebScript.sanitize(input);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testSanitizeRemovesOnclick() {
        String input = "<p onclick=\"alert(\"bad\");\">The quick brown fox jumps over the lazy dog</p>";
        String expected = "The quick brown fox jumps over the lazy dog";
        String sanitized = commentsWebScript.sanitize(input);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testSanitizeRemovesHTMLPage() {
        String input = "<!DOCTYPE html><html><body><p>Click the button to display an alert box:</p><button onclick=\"myFunction()\">Try it</button><script>function myFunction() { alert(\"I am an alert box!\");}</script></body></html>";
        String expected = "Click the button to display an alert box:Try it";
        String sanitized = commentsWebScript.sanitize(input);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testTextRemoveInLinks() {
        String input = "<a href=\"../good.html\">click here</a>";
        String expected = "click here";
        String sanitized = commentsWebScript.sanitize(input);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testtestShould_escape_LTGT_when_possible() {
        String html = "< bla >";
        String expected = "< bla >";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShould_remove_textarea() {
        String html = "<textarea>";
        String expected = "";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShould_remove_javascript_from_href() {
        String html = "<a href=\"javascript:\">";
        String expected = "";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }


    @Test
    public void testShouldAddNoFollowIntoLinks() {
        String html = "<a href=\"http://www.teste.com.br\">teste</a>";
        String expected = "teste";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldAddTargetBlankIntoLinks() {
        String html = "<a href=\"http://www.teste.com.br\" rel=\"nofollow\">teste</a>";
        String expected = "teste";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldChangeTargetToBlankInLinks() {
        String html = "<a href=\"http://www.teste.com.br\" rel=\"nofollow\" target=\"_self\">teste</a>";
        String expected = "teste";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveLinksWithInvalidProtocol() {
        String html = "<a href=\"ftp://www.teste.com.br\">teste</a>";
        String expected = "teste";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagScript() {
        String html = "<script>function deleteAll(){document.getElementsByTagName(\"body\")[0].remove()}</script>";
        String sanitized = commentsWebScript.sanitize(html);
        assertTrue(sanitized.isEmpty());
    }

    @Test
    public void testShouldRemoveInvalidAttributesOfALink() {
        String html = "<a class=\"my-class\" href=\"http://www.teste.com.br\" target=\"_blank\">teste</a>";
        String expected = "teste";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagP() {
        String html = "<p>My text maroto</p>";
        String expected = "My text maroto";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagPre() {
        String html = "<pre>My code maroto</pre>";
        String expected = "My code maroto";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldNotRemoveTagCode() {
        String html = "<code>My inline code maroto</code>";
        String expected = "My inline code maroto";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagImg() {
        String html = "<img src=\"myimage.png\" alt=\"chrome\" />Image was here";
        String expected = "Image was here";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldNotAllowImagesSize() {
        String html = "<img src=\"http://www.teste.com.br\" alt=\"x\" width=\"5\" height=\"3\" />Image was here\n";
        String sanitized = commentsWebScript.sanitize(html);
        String expected = "Image was here";
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldNotAllowOtherAttributesForImages() {
        String html = "<img src=\"http://www.teste.com.br\" alt=\"x\" width=\"5\" height=\"3\" somethingElse=\"foo\" />Image was here";
        String expected = "Image was here";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }


    @Test
    public void testShouldRemoveTagKbd() {
        String html = "<kbd>shift</kbd>";
        String expected = "shift";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagOl() {
        String html = "<ol><li>shift</li><li>ctrl</li></ol>";
        String expected = "shift ctrl";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagUl() {
        String html = "<ul><li>shift</li><li>ctrl</li></ul>";
        String expected = "shift ctrl";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagStrong() {
        String html = "<strong>leo</strong>";
        String expected = "leo";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagH2() {
        String html = "<h2>Title</h2>";
        String expected = "Title";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldRemoveTagBlockquote() {
        String html = "<blockquote>My quotation</blockquote>";
        String expected = "My quotation";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShouldNotRemoveTagHr() {
        String html = "<hr />";
        String expected = "";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testShould_remove_tag_iframe() {
        String html = "<ol><li><iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" src=\"https://www.test.url\"></iframe></li><li>ctrl</li></ol>";
        String htmlSanitized = "ctrl";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(htmlSanitized, sanitized);
    }

    @Test
    public void testShould_remove_script_tag() {
        String html = "<script>alert('bla');</script></li><li>ctrl</li></ol>";
        String htmlSanitized = "ctrl";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(htmlSanitized, sanitized);
    }

    @Test
    public void testShould_return_empty_sanitized_text_if_null() {
        String html = null;
        String htmlSanitized = null;
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(htmlSanitized, sanitized);
    }

    @Test
    public void testShould_return_empty_sanitized_text_if_empty() {
        String html = "";
        String expected = "";
        String sanitized = commentsWebScript.sanitize(html);
        assertEquals(expected, sanitized);
    }
}
