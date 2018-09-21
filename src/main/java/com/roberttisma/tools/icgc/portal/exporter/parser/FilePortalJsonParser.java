/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.roberttisma.tools.icgc.portal.exporter.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.ACCESS;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.ANALYSIS_METHOD;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.DATA_CATEGORIZATION;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.DATA_TYPE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.DONORS;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.DONOR_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.EXPERIMENTAL_STRATEGY;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.FILE_COPIES;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.FILE_FORMAT;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.FILE_MD5SUM;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.FILE_NAME;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.FILE_SIZE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.GENOME_BUILD;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.INDEX_FILE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.LAST_MODIFIED;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.OBJECT_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.PROJECT_CODE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.REFERENCE_GENOME;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.REFERENCE_NAME;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.REPO_DATA_BUNDLE_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.REPO_METADATA_PATH;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.REPO_NAME;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SAMPLE_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SOFTWARE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SPECIMEN_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SPECIMEN_TYPE;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SUBMITTED_DONOR_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SUBMITTED_SAMPLE_ID;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FieldNames.SUBMITTED_SPECIMEN_ID;
import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Streams.stream;

@NoArgsConstructor(access = PRIVATE)
public final class FilePortalJsonParser {

  public static String getAccess(@NonNull JsonNode file){
    return file.path(ACCESS).textValue();
  }

  public static String getObjectId(@NonNull JsonNode file) {
    return file.path(OBJECT_ID).textValue();
  }

  public static String getDataType(@NonNull JsonNode file) {
    return getDataCategorization(file).path(DATA_TYPE).textValue();
  }

  public static String getExperimentalStrategy(@NonNull JsonNode file) {
    return getDataCategorization(file).path(EXPERIMENTAL_STRATEGY).textValue();
  }

  public static String getFileId(@NonNull JsonNode file) {
    return file.path(ID).textValue();
  }

  public static String getFileName(@NonNull JsonNode file) {
    return getFirstFileCopy(file).path(FILE_NAME).textValue();
  }

  public static String getFileFormat(@NonNull JsonNode file) {
    return getFirstFileCopy(file).path(FILE_FORMAT).textValue();
  }

  public static long getFileSize(@NonNull JsonNode file) {
    return getFirstFileCopy(file).path(FILE_SIZE).asLong(-1);
  }

  public static long getFileLastModified(@NonNull JsonNode file) {
    return getFirstFileCopy(file).path(LAST_MODIFIED).asLong(-1);
  }

  public static String getFileMd5sum(@NonNull JsonNode file) {
    return getFirstFileCopy(file).path(FILE_MD5SUM).textValue();
  }

  public static String getProjectCode(@NonNull JsonNode file) {
    return getFirstDonor(file).path(PROJECT_CODE).textValue();
  }

  public static String getDonorId(@NonNull JsonNode file) {
    return getFirstDonor(file).path(DONOR_ID).textValue();
  }

  public static String getSubmittedDonorId(@NonNull JsonNode file) {
    return getFirstDonor(file).path(SUBMITTED_DONOR_ID).textValue();
  }

  public static String getReferenceName(@NonNull JsonNode file) {
    return getReferenceGenome(file).path(REFERENCE_NAME).textValue();
  }

  public static String getGenomeBuild(@NonNull JsonNode file) {
    return getReferenceGenome(file).path(GENOME_BUILD).textValue();
  }

  private static JsonNode getReferenceGenome(@NonNull JsonNode file) {
    return file.path(REFERENCE_GENOME);
  }

  public static String getRepoDataBundleId(@NonNull JsonNode file, @NonNull String repoName){
    return getRepoFileCopy(file, repoName).path(REPO_DATA_BUNDLE_ID).textValue();
  }

  public static String getRepoMetadataPath(@NonNull JsonNode file, @NonNull String repoName){
    return getRepoFileCopy(file, repoName).path(REPO_METADATA_PATH).textValue();
  }

  public static Optional<String> getIndexFileId(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(ID).textValue());
  }


  public static Optional<String> getIndexFileObjectId(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(OBJECT_ID).textValue());
  }

  public static Optional<String> getIndexFileFileName(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(FILE_NAME).textValue());
  }

  public static Optional<String> getIndexFileFileFormat(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(FILE_FORMAT).textValue());
  }

  public static Optional<String> getIndexFileFileMd5sum(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(FILE_MD5SUM).textValue());
  }

  public static Optional<Long> getIndexFileFileSize(@NonNull JsonNode file){
    return getIndexFile(file, x -> x.path(FILE_SIZE).asLong(-1));
  }

  public static String getSoftware(@NonNull JsonNode file){
    return getAnalysisMethod(file).path(SOFTWARE).textValue();
  }

  public static List<String> getSampleIds(@NonNull JsonNode file) {
    val sampleIdNode = getFirstDonor(file).path(SAMPLE_ID);
    return getFirstLevelList(sampleIdNode);
  }

  public static List<String> getSubmittedSampleIds(@NonNull JsonNode file) {
    val submittedSampleIdNode = getFirstDonor(file).path(SUBMITTED_SAMPLE_ID);
    return getFirstLevelList(submittedSampleIdNode);
  }

  public static List<String> getSpecimenIds(@NonNull JsonNode file) {
    val specimenIdNode = getFirstDonor(file).path(SPECIMEN_ID);
    return getFirstLevelList(specimenIdNode);
  }

  public static List<String> getSpecimenTypes(@NonNull JsonNode file) {
    val specimenIdNode = getFirstDonor(file).path(SPECIMEN_TYPE);
    return getFirstLevelList(specimenIdNode);
  }

  public static List<String> getSubmittedSpecimenIds(@NonNull JsonNode file) {
    val submittedSpecimenIdNode = getFirstDonor(file).path(SUBMITTED_SPECIMEN_ID);
    return getFirstLevelList(submittedSpecimenIdNode);
  }

  private static List<String> getFirstLevelList(@NonNull JsonNode node){
    return stream(node)
        .map(JsonNode::textValue)
        .collect(toImmutableList());
  }

  private static JsonNode getFirstDonor(@NonNull JsonNode file) {
    return getDonors(file).path(0);
  }

  private static JsonNode getFirstFileCopy(@NonNull JsonNode file) {
    return getFileCopies(file).path(0);
  }

  private static JsonNode getRepoFileCopy(@NonNull JsonNode file, @NonNull String repoName) {
    val fileCopies = getFileCopies(file);
    checkState(fileCopies.isArray(), "The object '%s' is not an array", fileCopies.toString() );
    for (val fileCopy : fileCopies){
      if (fileCopy.has(REPO_NAME) && fileCopy.get(REPO_NAME).textValue().equals(repoName)){
        return fileCopy;
      }
    }
    throw new IllegalStateException(format("Could not find the repoName '%s'", repoName));
  }

  private static JsonNode getDataCategorization(@NonNull JsonNode file) {
    return file.path(DATA_CATEGORIZATION);
  }

  private static JsonNode getFileCopies(@NonNull JsonNode file) {
    return file.path(FILE_COPIES);
  }

  public static JsonNode getDonors(@NonNull JsonNode file) {
    return file.path(DONORS);
  }

  private static <T> Optional<T> getIndexFile(@NonNull JsonNode file, Function<JsonNode, T> extractFunction){
    val opt = Optional.ofNullable(getFirstFileCopy(file).path(INDEX_FILE));
    return opt.map(extractFunction);
  }

  private static JsonNode getAnalysisMethod(@NonNull JsonNode file){
    return file.path(ANALYSIS_METHOD);
  }
}
