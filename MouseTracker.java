package edu.mco364;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

class ConsoleObserver implements Observer
{
    @Override
    public void update(Observable o, Object arg) {
        System.out.println(arg);
    }
}

class NetworkObserver implements Observer
{
    @Override
    public void update(Observable o, Object arg) {
    }
}

public class MouseTracker extends JFrame {
    public static void main(String[] args) {
        MouseTracker app = new MouseTracker();
        app.addListener(new ConsoleObserver());
    }
    MouseTracker()
    {
        setSize( 300, 150 ); // set size of window
        setVisible( true ); // show window
        setDefaultCloseOperation(3);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                observable.notifyObservers(e);
            }
        });
    }
    public class MTO extends Observable
    {
        public void notifyObservers(Object e)
        {
            this.setChanged();
            super.notifyObservers(e);
        }
    }
    public void addListener(Observer o)
    {
        observable.addObserver(o);
    }

    private Observable observable = new MTO();
}
