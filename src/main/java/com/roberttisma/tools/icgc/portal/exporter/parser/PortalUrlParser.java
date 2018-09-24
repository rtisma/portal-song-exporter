package com.roberttisma.tools.icgc.portal.exporter.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public class PortalUrlParser {

  private static final String REPOSITORIES_ENDPOINT = "/repositories";
  private static final String FILTERS = "filters";

  @NonNull private final String portalApiUrl;

  @SneakyThrows
  public String extractJQLQuery(String url){
    val urlPrefix = portalApiUrl+REPOSITORIES_ENDPOINT;
    val finalUrl = resolveFinalUrl(url);
    checkArgument(finalUrl.startsWith(urlPrefix),
        "The url '%s' does not start with '%s'", finalUrl, urlPrefix);

    val encodedParams = finalUrl.replaceFirst(urlPrefix+"\\?", "");
    val decodedParams = decode(encodedParams);
    val keypairs = stream(decodedParams.split("&"))
        .map(x -> x.split("="))
        .collect(toMap(x -> x[0], PortalUrlParser::joinAllButFirst));
    checkArgument(keypairs.containsKey(FILTERS),
        "The decoded url params '%s' does not contain the '%s' key", decodedParams, FILTERS);
    return keypairs.get(FILTERS);
  }

  private static String joinAllButFirst(String[] a){
    return joinSubArray(a, 1, a.length);
  }

  private static String joinSubArray(String[] a, int startInc, int endExc){
    return stream(a)
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

  public static PortalUrlParser createPortalUrlParser(String portalApiUrl) {
    return new PortalUrlParser(portalApiUrl.replaceAll("[/]+$", ""));
  }

}
