package com.roberttisma.tools.icgc.portal.exporter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static com.roberttisma.tools.icgc.portal.exporter.Config.PORTAL_API_URL;
import static com.roberttisma.tools.icgc.portal.exporter.parser.PortalUrlParser.extractJQLQuery;
import static java.nio.file.Files.readAllBytes;

@Slf4j
public class Main {

  private static final long TIMESTAMP = System.currentTimeMillis();

  @Option(name="--threads", usage = "Number of threads to use. Default 2", metaVar = "<int>")
  private int numThreads = 2;

  @Option(name="--output-dir", usage = "Output directory. Default is output.<timestamp>. Will create the directory if it doesnt exist",
      metaVar = "DIR")
  private String outputDirname = "./output."+TIMESTAMP;

  @Option(name="--portal-api-url", usage = "Url to ICGC-DCC portal. Default is '"+PORTAL_API_URL+"'")
  private String portalApiUrl =  PORTAL_API_URL;

  @Option(name="--portal-repo-name", usage = "Repo name corresponding to the input query",required = true)
  private String portalRepoName;

  @Option(name="--song-url", usage = "SONG url for retrieving object metadata",required = true)
  private String songUrl;

  @Option(name="--query-url", usage = "ICGC-DCC portal repositories query url",required = true)
  private String queryUrl;

  @Option(name="--help",usage="print this message")
  private boolean help = false;

  @Option(name="-h")
  private void setHelp(boolean h) {
    this.help = h;
  }


  private void doMain(String[] args){
    val parser = new CmdLineParser(this);
    parser.setUsageWidth(80);

    try{
      parser.parseArgument(args);
      if (help) {

        val  baos = new ByteArrayOutputStream();
        parser.printUsage(baos);
        System.out.println("Usage "+baos.toString());
      } else {
        val factory = Factory.builder()
            .batchSize(Config.BATCH_SIZE)
            .numThreads(numThreads)
            .outputDirpath(Paths.get(outputDirname))
            .portalApiUrl(portalApiUrl)
            .portalFetchSize(Config.PORTAL_FETCH_SIZE)
            .portalRepoName(portalRepoName)
            .songServerUrl(songUrl)
            .jqlQuery(extractJQLQuery(queryUrl))
            .build();
        val portalExtractor = factory.buildPortalExtractor();
        portalExtractor.run();
      }
    } catch (CmdLineException e){
      if (help){
        System.out.println("Usage: ");
        parser.printSingleLineUsage(System.out);
        System.out.println();
        parser.printUsage(System.out);
        System.out.println();
      } else {
        System.err.println(e.getMessage());
        System.err.println();
        parser.printSingleLineUsage(System.err);
        System.out.println();
        parser.printUsage(System.err);
        System.err.println();
      }
      return;
    }
  }

  @SneakyThrows
  public static void main(String[] args){
    new Main().doMain(args);
//    val jqlQuery = extractJQLQuery(args[0]);
//    val factory = Factory.builder()
//        .batchSize(Config.BATCH_SIZE)
//        .numThreads(Config.NUM_THREADS)
//        .outputDirpath(Config.OUTPUT_DIRPATH)
//        .portalApiUrl(PORTAL_API_URL)
//        .portalFetchSize(Config.PORTAL_FETCH_SIZE)
//        .portalRepoName(Config.PORTAL_REPO_NAME)
//        .songServerUrl(Config.SONG_COLLAB_URL)
//        .jqlQuery(jqlQuery)
//        .build();
//    val portalExtractor = factory.buildPortalExtractor();
//    portalExtractor.run();
  }

  private static Path getFileFromArgument(String[] args){
    val filename = args[0];
    val path = Paths.get(filename);
    checkArgument(Files.exists(path), "The input filename '%s' does not exist",  filename);
    checkArgument(Files.isRegularFile(path) && Files.isReadable(path), "The input file '%s' is not a regular file or readable", filename);
    return path;
  }

  @SneakyThrows
  private static String readFile(Path p){
    return new String(readAllBytes(p));
  }

}
