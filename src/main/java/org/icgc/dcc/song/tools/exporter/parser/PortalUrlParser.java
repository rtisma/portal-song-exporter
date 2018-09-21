package org.icgc.dcc.song.tools.exporter.parser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

@Slf4j
public class PortalUrlParser {

  private static final String PREFIX = "https://dcc.icgc.org/repositories";
  private static final String FILTERS = "filters";

  @SneakyThrows
  public static String extractJQLQuery(String url){
    val finalUrl = resolveFinalUrl(url);
    checkArgument(finalUrl.startsWith(PREFIX),
        "The url '%s' does not start with '%s'", finalUrl, PREFIX);

    val encodedParams = finalUrl.replaceFirst(PREFIX+"\\?", "");
    val decodedParams = decode(encodedParams);
    val keypairs = Arrays.stream(decodedParams.split("&"))
        .map(x -> x.split("="))
        .collect(Collectors.toMap(x -> x[0], PortalUrlParser::joinAllButFirst));
    checkArgument(keypairs.containsKey(FILTERS),
        "The decoded url params '%s' does not contain the '%s' key", decodedParams, FILTERS);
    return keypairs.get(FILTERS);
  }

  private static String joinAllButFirst(String[] a){
    return joinSubArray(a, 1, a.length);
  }

  private static String joinSubArray(String[] a, int startInc, int endExc){
    return Arrays.stream(a)
        .limit(endExc)
        .skip(startInc)
        .collect(joining("="));

  }

  @SneakyThrows
  private static String decode(String input){
    return URLDecoder.decode(input, "UTF-8");
  }

  @SneakyThrows
  private static String resolveFinalUrl(String url){
    String location = url;
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setInstanceFollowRedirects(false);
    while (connection.getResponseCode() / 100 == 3) {
      location = connection.getHeaderField("location");
      connection = (HttpURLConnection) new URL(location).openConnection();
    }
    return location;
  }

}
