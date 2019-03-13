package edu.mco364;

import com.sun.javafx.image.IntPixelGetter;

import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

public abstract class NetworkApp extends JFrame
{
    protected static final int WINDOW_HEIGHT = 600;
    protected static final int WINDOW_WIDTH = 800;
    private static final int LEFT_PADDLE_X = 20;
    private static final int RIGHT_PADDLE_X = WINDOW_WIDTH - LEFT_PADDLE_X;
    private static final int PADDLE_HEIGHT = 100;
    private static final int PADDLE_CENTER = WINDOW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private static final int PADDLE_WIDTH =10, BALL_SIZE = 30;
    private JPanel enterField; // enters information from user
    private JButton startButton;
    private ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server
    private String message = ""; // message from server
    protected Socket connection; // socket to communicate with server

    Point ball = new Point(100,100),
            paddleLeft = new Point(LEFT_PADDLE_X, PADDLE_CENTER),
            paddleRight = new Point(RIGHT_PADDLE_X, PADDLE_CENTER),
            delta = new Point(1,1);

    // initialize chatServer and set up GUI
    public NetworkApp(String title)
    {
        super(title);
        enterField = new JPanel(); // create enterField

        enterField.addMouseWheelListener(
                new MouseWheelListener() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        sendData( e.getWheelRotation());
                        updatePaddle(isLeftPaddleActive(), e.getWheelRotation());
                    }
                }
        );

        add( enterField, BorderLayout.CENTER );

        startButton = new JButton("Start");
        add( startButton, BorderLayout.SOUTH );

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData("Start");
                startGame();
            }
        });
        setUndecorated(true);
        setSize( WINDOW_WIDTH, WINDOW_HEIGHT ); // set size of window
        setVisible( true ); // show window
        setResizable(false);
    }
    private Timer ballTimer;
    private void startGame() {
        ballTimer = new Timer(10, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ball.translate(delta.x, delta.y);
                repaint();
            }
        }
        );
        ballTimer.start();
    }

    protected abstract boolean  isLeftPaddleActive();

    private void updatePaddle(boolean isLeft, int moveY) {
        (isLeft ? paddleLeft : paddleRight).y += moveY;
        repaint();
    }

    private void sendData( Object o )
    {
        try
        {
            output.writeObject( o );
            output.flush();
        }
        catch ( IOException ioException )
        {
        }
    }


    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream( connection.getInputStream() );

        System.out.println( "\nGot I/O streams\n" );
    }

    private void processConnection() throws IOException
    {
        do // process messages sent from server
        {
            try // read message and display it
            {
                Object o = input.readObject();
                if (o instanceof Integer)
                {
                    updatePaddle(! isLeftPaddleActive(), (int) o);
                }
                else if (o instanceof Point)
                {
                    ball = (Point) o;
                }
                else if (o instanceof String)
                {
                    startButton.setEnabled(false);
                    startGame();
                }

                repaint();

            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                System.out.println( "\nUnknown object type received" );
            } // end catch

        } while ( !message.equals( "SERVER>>> TERMINATE" ) );
    } // end method processConnection

    // close streams and socket
    private void closeConnection()
    {
        System.out.println( "\nClosing connection" );

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

    public void paint(Graphics g) {
        super.paint(g);
        g.fillOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
        g.fillRect(paddleLeft.x, paddleLeft.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(paddleRight.x, paddleRight.y, PADDLE_WIDTH, PADDLE_HEIGHT);
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
            System.out.println( "\nClient terminated connection" );
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