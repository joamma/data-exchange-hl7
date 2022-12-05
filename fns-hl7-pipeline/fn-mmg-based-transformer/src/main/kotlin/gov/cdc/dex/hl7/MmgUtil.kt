package gov.cdc.dex.hl7

import com.google.gson.Gson

import gov.cdc.dex.redisModels.MMG

import gov.cdc.dex.hl7.model.ConditionCode
import gov.cdc.dex.hl7.model.SpecialCase

import gov.cdc.hl7.HL7StaticParser
import org.slf4j.LoggerFactory
import java.util.*

// import redis.clients.jedis.DefaultJedisClientConfig
// import redis.clients.jedis.Jedis

import  gov.cdc.dex.mmg.MmgUtil as MmgUtilLib
import  gov.cdc.dex.azure.RedisProxy 

class MmgUtil(val redisProxy: RedisProxy)  {

    companion object {
        val logger = LoggerFactory.getLogger(MmgUtil::class.java.simpleName)

        const val PATH_MSH_21_2_1 = "MSH-21[2].1" // Gen 
        const val PATH_MSH_21_3_1 = "MSH-21[3].1" // Condition
        const val EVENT_CODE_PATH = "OBR[@4.1='68991-9']-31.1"

        const val PATH_JURISDICTION_CODE = "" // TODO: complete path

        val REDIS_CACHE_NAME = System.getenv("REDIS_CACHE_NAME")
        val REDIS_PWD = System.getenv("REDIS_CACHE_KEY")

        const val REDIS_MMG_PREFIX = "mmg:"
        const val REDIS_CONDITION_PREFIX = "condition:"

        private val gson = Gson()
    } // .companion

        
        @Throws(Exception::class)
        fun getMMGFromMessage(message: String, filePath: String, messageUUID: String): Array<MMG> {

            val msh21_2 = extractValue(message, PATH_MSH_21_2_1).lowercase(Locale.getDefault())
            val msh21_3 = extractValue(message, PATH_MSH_21_3_1).lowercase(Locale.getDefault())
            val eventCode = extractValue(message, EVENT_CODE_PATH)
            val jurisdictionCode = "23" // TODO: ..  extractValue(message, PATH_JURISDICTION_CODE)

            logger.info("getMMGFromMessage, for message filePath ${filePath}, messageUUID: ${messageUUID} --> msh21_2: $msh21_2, msh21_3: $msh21_3, eventCode: $eventCode")

            val mmgUtilLib = MmgUtilLib(redisProxy)

            return mmgUtilLib.getMMG(msh21_2, msh21_3, eventCode, jurisdictionCode) //getMMG(msh21_2, msh21_3, eventCode, jurisdictionCode)
        } // .getMMGFromMessage

        private fun extractValue(msg: String, path: String):String  {
            val value = HL7StaticParser.getFirstValue(msg, path)
            return if (value.isDefined) value.get() //throw Exception("Error extracting $path from HL7 message")
                else ""
        } // .extractValue


} // .MmgUtil
