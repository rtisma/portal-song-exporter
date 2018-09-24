Portal Song Exporter
---

This program takes a portal repository url, extracts all the files and reduces to a list of analysisIds, and then uses those analysisIds to export payloads using the song api.


### Building
Ensure you have java 8 and maven 3.5.2 installed, then in the root directory run:

```bash
mvn clean package
```

### Running

For usage, run the following command to output the help menu:

```bash
java -jar target/*-jar-with-dependencies.jar   -h

```

which outputs:

```
Usage: 
  [--help] [--output-dir DIR] [--portal-api-url VAL] --portal-repo-name VAL --query-url VAL --song-url VAL [--threads <int>] [-h]
  --help                 : print this message (default: true)
  --output-dir DIR       : Output directory. Default is output.<timestamp>. Will
  create the directory if it doesnt exist (default:
  ./output.1537807968959)
  --portal-api-url VAL   : Url to ICGC-DCC portal. Default is
  'https://dcc.icgc.org' (default: https://dcc.icgc.org)
  --portal-repo-name VAL : Repo name corresponding to the input query
  --query-url VAL        : ICGC-DCC portal repositories query url
  --song-url VAL         : SONG url for retrieving object metadata
  --threads <int>        : Number of threads to use. Default 2 (default: 2)

```

The organization of the `--output-dir` directory is `/<studyId>/<analysisId>.json` where `<studyId>` is a directory and `<analysisId>.json` is a file. There can be multiple files per studyId directory.

### Example

To process https://dcc.icgc.org repository links, the following command can be run using a shortened portal url:

```bash
java -jar target/*-jar-with-dependencies.jar \
    --song-url 'https://song.cancercollaboratory.org' \
    --portal-repo-name 'Collaboratory - Toronto' \
    --threads 7 \
    --portal-api-url 'https://dcc.icgc.org'  \
    --output-dir my_output_dir \
    --query-url 'https://icgc.org/Zzy'
```

or a regular portal url:

```bash
java -jar target/*-jar-with-dependencies.jar \
    --song-url 'https://song.cancercollaboratory.org' \
    --portal-repo-name 'Collaboratory - Toronto' \
    --threads 7 \
    --portal-api-url 'https://dcc.icgc.org'  \
    --output-dir my_output_dir \
    --query-url 'https://dcc.icgc.org/repositories?filters=%7B%22file%22:%7B%22repoName%22:%7B%22is%22:%5B%22Collaboratory%20-%20Toronto%22%5D%7D%7D%7D&files=%7B%22from%22:1,%22size%22:25%7D'
```

### Notes
1. The number of files shown in portal might not match the number of files exported. Since the software extracts the analysisId from the resulting portal files, those analyses may have files that do not fit the filter criteria from the original portal query, which could lead to a difference in numbers

