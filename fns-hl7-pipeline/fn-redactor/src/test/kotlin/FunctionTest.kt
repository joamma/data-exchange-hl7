import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gov.cdc.dex.hl7.Helper
import gov.cdc.dex.hl7.model.RedactorProcessMetadata
import gov.cdc.dex.metadata.DexMetadata
import gov.cdc.dex.metadata.Provenance
import gov.cdc.dex.util.JsonHelper.addArrayElement
import org.junit.jupiter.api.Test

class FunctionTest {

    @Test
    fun TestRedactor(){
        val msg = this::class.java.getResource("/sample.txt")?.readText()
        val helper = Helper()
        val report = msg?.let { helper.getRedactedReport(it) }
        if (report != null) {
            println("report msg :${report._1}")
            println("report list:${report._2}")
        }

    }
@Test
fun TestMetaData(){

    val gson = GsonBuilder().serializeNulls().create()

    val REDACTOR_STATUS_OK = "PROCESS_REDACTOR_OK"
    val msg = this::class.java.getResource("/sample.txt").readText()
    val helper = Helper()
    val report =  helper.getRedactedReport(msg)
    val w = report?._2()?.toList()

    println("w: ${w}")
//    gson.toJson(w)
//    val redactJson = w?.toJsonElement()
//
//    println("redactJson: $redactJson")

    val processMD = RedactorProcessMetadata("OK", w)

    println(processMD)

    val prov = Provenance("event1", "123", "123", "test", "123", 123, "single", "a", "b", "c', 1")
    val md = DexMetadata(prov, listOf())

    val mdJsonStr = gson.toJson(md)

    val mdJson = JsonParser.parseString(mdJsonStr) as JsonObject
    mdJson.addArrayElement("processes" , processMD)
     mdJson.addProperty("test","test")

    println("MD: $mdJson")

}


}

