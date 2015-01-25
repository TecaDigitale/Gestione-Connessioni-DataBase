/**
 * 
 */
package mx.database.struttura.createTables;

import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.struttura.xsd.Constraint;
import mx.database.struttura.xsd.View;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class CreateConstraint
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(CreateConstraint.class, "mx.databaseStruttura");

	/**
	 * 
	 */
	public CreateConstraint()
	{
	}

	public static void createConstraint(ConnectionPool pool, String tableName, Constraint constraint)
	{
		String scriptSQL = null;
		MsSqlPool msp = null;
		
		try
		{
			
			scriptSQL = "alter table "+tableName+
			            " add constraint "+constraint.getName()+ 
			            " foreign key ("+constraint.getColumn().getValue()+
			            ") references "+constraint.getTable().getName()+" ("+constraint.getTable().getColumn().get(0).getValue()+");";
			msp = pool.getConn();
			msp.esegui(scriptSQL);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			if (msp != null)
				pool.releaseConn(msp);
		}
	}
}
