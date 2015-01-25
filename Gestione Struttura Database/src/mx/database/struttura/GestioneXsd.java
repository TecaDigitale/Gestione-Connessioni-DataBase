package mx.database.struttura;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import mx.log4j.Logger;

public class GestioneXsd 
{

	private static Logger log = new Logger(GestioneXsd.class, "it.siav.teca.export");

	public static Object readXml(String fileXml, String packageName) throws JAXBException
	{
		Unmarshaller um = null;
		JAXBContext jc = null;
		Object output = null;
		File f = null;

		try
		{
			log.debug("readXml()");

			f = new File(fileXml);
			if (f.exists())
			{
				log.debug("Utente.package: "+packageName);
				jc = JAXBContext.newInstance( packageName );

				um = jc.createUnmarshaller();

				output = um.unmarshal(f);
			
			}
		}
		catch (JAXBException e)
		{
			throw e;
		}
		return output;
	}

	public static void writeXml(String fileXml, Object datiXml) throws PropertyException, JAXBException, IOException, Exception
	{
		Marshaller m = null;
		JAXBContext jc = null;
		File fOut = null;
		FileOutputStream fos = null;
		
		try
		{
			log.debug("write()");

			log.debug("Utente.package: "+datiXml.getClass().getPackage().getName());
			jc = JAXBContext.newInstance(datiXml.getClass().getPackage().getName());

			log.debug("jc.createUnmarshaller()");
			m = jc.createMarshaller();

			log.debug("m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

			log.debug("fileXml: "+fileXml);
			fOut =  new File(fileXml);
			if (fOut.getParentFile() != null && !fOut.getParentFile().exists())
				if (!fOut.getParentFile().mkdirs())
					throw new Exception("Problemi nella creazone della cartella ["+fOut.getParentFile().getAbsolutePath()+"]");

			log.debug("fOut.exists(): "+fOut.exists());

			log.debug("FileOutputStream(fOut)");
			fos = new FileOutputStream(fOut);

			log.debug("m.marshal( utente, fos )");
			m.marshal( datiXml, fos );
		}
		catch (PropertyException e)
		{
			log.error(e);
			throw e;
		}
		catch (JAXBException e)
		{
			log.error(e);
			throw e;
		}
		catch (Exception e)
		{
			log.error(e);
			throw e;
		}
		finally
		{
			try
			{
				if (fos != null)
				{
					fos.flush();
					fos.close();
				}
			}
			catch (IOException e)
			{
				log.error(e);
				throw e;
			}
		}
	}
}
