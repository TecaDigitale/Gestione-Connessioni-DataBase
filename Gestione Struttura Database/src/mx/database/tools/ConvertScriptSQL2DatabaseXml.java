/**
 * 
 */
package mx.database.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import mx.database.struttura.GestioneXsd;
import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Database;
import mx.database.tools.convertScriptSQL2DatabaseXml.AlterTable;
import mx.database.tools.convertScriptSQL2DatabaseXml.CreateIndex;
import mx.database.tools.convertScriptSQL2DatabaseXml.CreateTable;
import mx.database.tools.convertScriptSQL2DatabaseXml.CreateView;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class ConvertScriptSQL2DatabaseXml
{

	/**
	 * Variabile utilizzata per la gestione degli errori dell'applicativo
	 */
	private Logger log = new Logger(ConvertScriptSQL2DatabaseXml.class, "mx.database.tools");

	/**
	 * 
	 */
	public ConvertScriptSQL2DatabaseXml()
	{
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ConvertScriptSQL2DatabaseXml convert = null;
		if (args.length==1)
		{
			convert = new ConvertScriptSQL2DatabaseXml();
			convert.esegui(args[0]);
		}
		else
			System.out.println("E' necessario indicare il nome del file tracciato Sql da analizzare");
	}

	public void esegui(String fileSql)
	{
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		Vector<String> lines = null;
		String line= "";
		Database database = null;
		
		try
		{
			f = new File(fileSql);
			
			if (f.exists())
			{
				log.info("Elaboro il file "+f.getAbsolutePath());
				database = new Database();
				fr  = new FileReader(f);
				br = new BufferedReader(fr);
				while ((line = br.readLine())!=null)
				{
					line = line.trim();
					if (!line.equals("") && !line.startsWith("/*"))
					{
						if (lines == null)
							lines = new Vector<String>();
						lines.add(line);
						if (line.endsWith(";"))
						{
							if (lines != null && lines.size()>0)
							{
								analizza(database, lines);
								lines.clear();
							}
						}
					}
				}
				if (lines != null && lines.size()>0)
				{
					analizza(database, lines);
					lines.clear();
				}
				GestioneXsd.writeXml(f.getAbsolutePath()+".xml", database);
			}
			else
				System.out.println("Il file ["+f.getAbsolutePath()+"] non esiste");
		}
		catch (FileNotFoundException e)
		{
			log.error(e);
		}
		catch (IOException e)
		{
			log.error(e);
		}
		catch (PropertyException e)
		{
			log.error(e);
		}
		catch (JAXBException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			}
			catch (IOException e)
			{
				log.error(e);
			}
		}
	}

	private void analizza(Database database, Vector<String> lines) throws DatabaseException
	{
		if (!lines.get(0).startsWith("drop"))
		{
  		if (lines.get(0).startsWith("create table"))
  			CreateTable.convert(database, lines);
  		else if (lines.get(0).startsWith("alter table"))
  			AlterTable.convert(database, lines);
  		else if (lines.get(0).startsWith("create index"))
  			CreateIndex.convert(database, lines);
  		else if (lines.get(0).startsWith("create view"))
  			CreateView.convert(database, lines);
  		else if (!lines.get(0).startsWith("create unique index"))
  			System.out.println(lines.toString());
		}
	}
}
