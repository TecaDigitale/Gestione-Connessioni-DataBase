package mx.database.tools.convertScriptSQL2DatabaseXml;

import java.util.Vector;

import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Database;
import mx.database.struttura.xsd.View;

public class CreateView
{

	/**
	 * 
	 */
	public CreateView()
	{
	}

	public static void convert(Database database, Vector<String> lines) throws DatabaseException
	{
		CreateView createView = null;
		createView = new CreateView();
		createView.esegui(database, lines);
	}

	public void esegui(Database database, Vector<String> lines) throws DatabaseException
	{
		View view = null;
		String line = "";

		view = new View();
		view.setName(lines.get(0).split(" ")[2]);
		for (int x=2; x<lines.size(); x++)
			line += (line.equals("")?"":" ")+lines.get(x);
		view.setValue(line);
		database.getView().add(view);
	}
}
