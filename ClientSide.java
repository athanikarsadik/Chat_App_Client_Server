import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;


//Type Exit to exit from chat

class GUIserver extends JFrame implements ActionListener
{
    JTextField text;
    JTextArea textarea;
    JLabel lable;
    JButton sendB;
    ClientSide cs;
    public GUIserver(ClientSide cs)
    {
        this.cs = cs;
        lable = new JLabel("Client");
        textarea = new JTextArea();
        text = new JTextField();
        sendB = new JButton("Send");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450,600);
        setLayout(new BorderLayout());
        addComponent();
        sendB.addActionListener(this);
        setVisible(true);
    }

    public void addComponent()
    {
        lable.setHorizontalTextPosition(SwingConstants.CENTER);
        lable.setHorizontalAlignment(SwingConstants.CENTER);
        lable.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        text.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   
        this.add(lable,BorderLayout.NORTH);
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(textarea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroll,BorderLayout.CENTER);
        JScrollPane scr2 = new JScrollPane(text);
        textarea.setBackground(Color.gray);
        this.add(scr2,BorderLayout.SOUTH);
        sendB.setBackground(Color.GREEN);
        this.add(sendB,BorderLayout.EAST);
    }
     
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        switch(s)
        {
            case "Send":
            if(text.getCaretPosition()!=0)
            {
                textarea.setText(textarea.getText()+"\nClient : "+"\n"+text.getText());
                String str;
                str = text.getText();
                cs.pw.println(str);
                cs.pw.flush();
                text.setText("");
            }
        }
    }
}

public class ClientSide
{
    Socket socket;
    BufferedReader sc ;
    PrintWriter pw ;
    GUIserver gui;

    public ClientSide() 
    {
        try{
            System.out.println("Client ready to accept conn ");
            socket = new Socket("localhost",5757);
            gui = new GUIserver(this);
            
            sc = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());

        }catch(Exception e){e.printStackTrace();}
         reading();
        // writing();
    }

    public void reading()
    {
        Runnable r1 = ()->{
            System.out.println("Reader..");

            try {
            while(true && !socket.isClosed())
            {
                String msg;
                msg = sc.readLine();
                gui.textarea.setText(gui.textarea.getText()+"\nServer : "+"\n"+msg);
                    if(msg.equals("Exit"))
                    {
                        System.out.println("Client Terminates the chat.");
                        socket.close();
                        System.exit(0);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        new Thread(r1).start();

    }

    public void writing()
    {
        Runnable r2 = ()->{
            System.out.println("Writer..");
            
            while(true && !socket.isClosed())
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s;
                try {
                    s = br.readLine();
                    pw.println(s);
                    pw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        new Thread(r2).start();
    }
    public static void main(String []args)
    {
        new ClientSide();  
    }
}