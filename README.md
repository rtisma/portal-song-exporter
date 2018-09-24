Portal Song Exporter
---

This program takes a portal repository url, extracts all the files and reduces to a list of analysisIds, and then uses those analysisIds to export payloads using the song api.


### Building
Ensure you have java 8 and maven 3.5.2 installed, then in the root directory run:

```bash
mvn clean package
```

### Running

```bash
java -jar target/*-jar-with-dependencies.jar  <portal_repository_url>

```

- the `portal_repository_url` can be a shortened url in the form of `https://icgc.org/<some_id>` which will automatically be resolved, or the normal url in the form of `https://dcc.icgc.org/repositories?filter=<encoded_jql_query>`.
- The output directory is fixed to `outputTest`. 
- The repository name is fixed to `Collaboratory - Toronto` which means only portal jql queries for the `Collaboratory - Toronto` repository name are allowed. 
- Runs concurrently with 6 threads
- the organization of the `outputTest` directory is `/<studyId>/<analysisId>.json` where `<studyId>` is a directory and `<analysisId>.json` is a file. There can be multiple files per studyId directory


### Notes
1. The number of files shown in portal might not match the number of files exported. Since the software extracts the analysisId from the resulting portal files, those analyses may have files that do not fit the filter criteria from the original portal query, which could lead to a difference in numbers

