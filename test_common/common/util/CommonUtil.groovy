package common.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javassist.bytecode.stackmap.BasicBlock.Catch;
import junit.framework.AssertionFailedError

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import groovy.io.FileType

import common.prism.CommonPrism

class CommonUtil {

	/*
	 * Tomcat Locations No Longer used after Spring Refactoring.
	 * Inbound and Outbound Locations set to project specific prism/...
	 */
//	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
//	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
//	def static final tomOutboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/outbound"

	def static final winInpath = "C:/dev/prism/ingestion/prism/inbound"
	def static final winOutpath = "C:/dev/prism/publish/prism/outbound"

	/**
	 *
	 */
	public static void deleteInbound() {
		deleteInbound(winInpath)
	}
	/**
	 *
	 * @param path
	 */
	public static void deleteInbound(def path) {
		if ( SystemUtils.IS_OS_WINDOWS )
		{
			//delete inbound file
			new File("${path}").eachFile { f -> f.delete() }
		}
		else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
	}

	/**
	 * Delete Outbound Directory at winOutpath for Windows system outbound path in publish
	 */
	public static void deleteOutbound() { deleteOutbound(winOutpath) }
	

	/**
	 *
	 * @param path
	 */
	public static void deleteOutbound(def path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			new File("${path}").eachFile { f -> f.delete() }
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}

	}


	/**
	 *
	 * @param resourceFile
	 * @param closetDir
	 */
	public static void copyResourceToInbound(File resourceFile, File inboundDir) {
		File resFile = CommonPrism.getResourceFile(resourceFile);
		FileUtils.copyFileToDirectory(resourceFile, inboundDir)

	}

	/**
	 * Deprecated.
	 * No longer getting resources outside of prism-system-testing.
	 * @param filename
	 * @return
	 */
	@Deprecated
	public static File getClosetFile(String filename) {
		File closFile;

		if (SystemUtils.IS_OS_WINDOWS) {
			//Copy from /common/.../resources to Tomca/prism/closet
			//closFile = new File("${tomClosetPath}/${filename}")
			assert closFile.exists()
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}

		return closFile;
	}


	/**
	 *
	 * @param file
	 * @param xml
	 */
	public static void writeXmlToFile(File file, def xml) {
		FileWriter writer = new FileWriter(file);
		BufferedWriter buff = new BufferedWriter(writer);
		buff.write(XmlUtil.serialize(xml))
		buff.close();
	}


}
