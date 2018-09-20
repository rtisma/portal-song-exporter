package org.icgc.dcc.song.tools.exporter;

import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Config {

  public static final String PORTAL_API_URL = "https://dcc.icgc.org";
  public static final String PORTAL_REPO_NAME = "Collaboratory - Toronto";
  public static final int NUM_THREADS = 6;
  public static final int PORTAL_FETCH_SIZE = 100;
  public static final int BATCH_SIZE = 100;
  public static final Path OUTPUT_DIRPATH = Paths.get("outputTest");
  public static final String SONG_COLLAB_URL = "https://song.cancercollaboratory.org";

}
