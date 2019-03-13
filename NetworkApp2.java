package edu.mco364;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class NetworkApp2 extends JFrame
{
    private final JPanel panel;
    private JTextField enterField; // enters information from user
    private JTextArea displayArea; // display information to user
    private ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server
    private String message = ""; // message from server
    protected Socket connection; // socket to communicate with server

    // initialize chatServer and set up GUI
    public NetworkApp2(String title)
    {
        super(title);

        displayArea = new JTextArea(); // create displayArea
        add( new JScrollPane( displayArea ), BorderLayout.NORTH );
        panel = new JPanel();
        add( panel, BorderLayout.CENTER );

        setSize( 600, 650 ); // set size of window
        setVisible( true ); // show window
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

    private void sendData( Point p )
    {
        try
        {
            output.writeObject( p );
            output.flush(); // flush data to output
            displayMessage( "\nCLIENT>>> " + message );
        }
        catch ( IOException ioException )
        {
            displayArea.append( "\nError writing object" );
        }
    }

    // manipulates displayArea in the event-dispatch thread
    protected void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater(
                () -> displayArea.append( messageToDisplay )
        );
    }

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable( final boolean editable )
    {
        SwingUtilities.invokeLater(
                () -> enterField.setEditable( editable ) // end anonymous inner class
        );
    }

    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream( connection.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    }

    private void processConnection() throws IOException
    {
        // enable enterField so client user can send messages
        setTextFieldEditable( true );

        do // process messages sent from server
        {
            try // read message and display it
            {
                message = ( String ) input.readObject(); // read new message
                displayMessage( "\n" + message ); // display message
            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                displayMessage( "\nUnknown object type received" );
            } // end catch

        } while ( !message.equals( "SERVER>>> TERMINATE" ) );
    } // end method processConnection

    // close streams and socket
    private void closeConnection()
    {
        displayMessage( "\nClosing connection" );
        setTextFieldEditable( false ); // disable enterField

        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            connection.close(); // close socket
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    }

    public void start()
    {
        try // connect to server, get streams, process connection
        {
            connect(); // create a Socket to make connection
            getStreams(); // get the input and output streams
            processConnection(); // process connection
        } // end try
        catch ( EOFException eofException )
        {
            displayMessage( "\nClient terminated connection" );
        } // end catch
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
        finally
        {
            closeConnection(); // close connection
        } // end finally
    } // end method runClient

    protected abstract void connect() throws IOException;
}