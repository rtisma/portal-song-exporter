package org.icgc.dcc.song.tools.exporter.parser;

import lombok.val;
import org.junit.Test;

public class ParserTest {

  @Test
  public void testParse(){

    val url = "https://icgc.org/ZbB";
    val url2 = "https://dcc.icgc.org/repositories?filters=%7B%22file%22%3A%7B%22repoName%22%3A%7B%22is%22%3A%5B%22Collaboratory%20-%20Toronto%22%2C%22AWS%20-%20Virginia%22%5D%7D%7D%7D&files=%7B%22from%22%3A1%2C%22size%22%3A25%7D";

    PortalUrlParser.extractJQLQuery(url2);

  }

}
