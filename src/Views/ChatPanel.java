package Views;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import Auxillary.ComponentExtras;
import Auxillary.VerticalLayout;
import Auxillary.utils;
import ClientServer.Chat;
import ClientServer.ChatMessage;
import ClientServer.MessagePack;
import ClientServer.NetInfo;
import ClientServer.User;
import Components.CircleImagePanel;
import Components.HintTextArea;
import Components.ImagePanel;


//Панель чата
public class ChatPanel extends JPanel {

	private NetInfo netInfo;
	final DefaultListModel<Chat> chats;
	private Chat chat;
	private ChatTitlePanel titlePanel = new ChatTitlePanel();
	private JPanel chatListBox = new JPanel();
	private HintTextArea newMessageField = new HintTextArea(5, 1, "Что думаете о происходящем?");
	private JLabel scrollDownPopup = new JLabel(new ImageIcon(utils.loadImageFromResources("scrolldown.png")));
    private JLayeredPane mainPanel = new JLayeredPane();


	public ChatPanel(NetInfo netInfo, DefaultListModel<Chat> chats) {
		super(new BorderLayout());
		createLayout();
		decoration();
		setResizeChatPanelOnReduce();
		setMessageTextFieldKeyListener();
		setOnScrollListener();
		setScrollDownPopupOnClick();
		setUndoRedo();
		this.netInfo = netInfo;
		this.chats = chats;
	}

	private JScrollPane scrollPanel;
	private void createLayout() {
		chatListBox.setLayout(new VerticalLayout(chatListBox));

		scrollPanel = new JScrollPane(chatListBox, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(5);
		scrollPanel.setBorder(null);
		
//		mainPanel.add(scrollPanel, new Integer(0), 0);
		add(mainPanel, BorderLayout.CENTER);
		add(titlePanel, BorderLayout.NORTH);

		
		scrollPanel.setOpaque(true);
        scrollDownPopup.setOpaque(false);
        scrollDownPopup.setVisible(false);
        scrollDownPopup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainPanel.add(scrollPanel, new Integer(0), 0);
        mainPanel.add(scrollDownPopup, new Integer(1), 0);
		
		
		
		newMessageField.setLineWrap(true);
		newMessageField.setWrapStyleWord(true);
		JScrollPane scrollPanel2 = new JScrollPane(newMessageField);
		scrollPanel2.setBorder(null);
		scrollPanel2.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(150, 150, 150)));
		add(scrollPanel2, BorderLayout.SOUTH);
	}

	private void decoration() {
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(150, 150, 150)));
		chatListBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		Border outsideBorder = BorderFactory.createMatteBorder(10, 10, 10, 10, chatListBox.getBackground());
		Border insideBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border border = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
		newMessageField.setBorder(border);

		chatListBox.setBackground(UIManager.getColor("List.background"));
	}

	// при уменьшении ширины chatPanel внутренний элемент - chatListBox - не уменьшается, приходится делать это вручную
	private boolean scroll_visible;
	private void setResizeChatPanelOnReduce() {
		class ResizeListener extends ComponentAdapter {
			public void componentResized(ComponentEvent e) {
				scroll_visible = scrollPanel.getVerticalScrollBar().isVisible();
				Dimension size = new Dimension(e.getComponent().getWidth() - 20, (int) chatListBox.getPreferredSize().getHeight());
				chatListBox.setSize(size);
				
				scrollPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
				scrollDownPopup.setBounds(mainPanel.getWidth() - 75, mainPanel.getHeight() - 75, 50, 50);
			}
		}
		addComponentListener(new ResizeListener());
		
		
		class ResizeListener2 extends ComponentAdapter {
			public void componentResized(ComponentEvent e) {
				Dimension size = new Dimension(e.getComponent().getWidth(),	(int) chatListBox.getLayout().preferredLayoutSize(chatListBox).getHeight());
				chatListBox.setPreferredSize(size);

				//изменение высоты прокручиваемого пространства скроллбара с сохранением позиции
				JScrollBar verticalBar = scrollPanel.getVerticalScrollBar();

				double percent = (double) verticalBar.getValue() / (double)(verticalBar.getMaximum() - verticalBar.getVisibleAmount());			
				verticalBar.setMaximum(size.height);
				verticalBar.setValue((int) ((verticalBar.getMaximum() - verticalBar.getVisibleAmount()) * percent));
				if (!scroll_visible && verticalBar.isVisible() || percent == 1) scrollToBottom(scrollPanel);
			}
		}
		chatListBox.addComponentListener(new ResizeListener2());
	}
	
	private ChatMessagePanel lastMessagePanel;
	private void setOnScrollListener() {
	    JScrollBar verticalBar = scrollPanel.getVerticalScrollBar();
	    AdjustmentListener downScroller = new AdjustmentListener() {
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	        	try
	        	{
		            Adjustable adjustable = e.getAdjustable();
		            int lastMsgPanelHeight = lastMessagePanel.getHeight();

		            if (!verticalBar.isVisible())
		            {
			            scrollDownPopup.setVisible(false);
			            return;
		            }

		            boolean enableScrollDown = adjustable.getMaximum() - (adjustable.getVisibleAmount() + adjustable.getValue()) > lastMsgPanelHeight;
		            if (scrollDownPopup.isVisible() != enableScrollDown) scrollDownPopup.setVisible(enableScrollDown);
	        	}
	        	catch(ArrayIndexOutOfBoundsException | NullPointerException ex) {}
	        }
	    };
	    verticalBar.addAdjustmentListener(downScroller);
	}
	
	private void setScrollDownPopupOnClick()
	{
		scrollDownPopup.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
	            JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();
	            scrollBar.setValue(scrollBar.getMaximum());
		    }  
		}); 
	}
	
	
	private final UndoManager undo = new UndoManager();
	private void setUndoRedo()
	{
		Document doc = newMessageField.getDocument();

		doc.addUndoableEditListener(new UndoableEditListener() {
		    public void undoableEditHappened(UndoableEditEvent evt) {
		        undo.addEdit(evt.getEdit());
		    }
		});

		newMessageField.getActionMap().put("Undo",
		    new AbstractAction("Undo") {
		        public void actionPerformed(ActionEvent evt) {
		            try {
		                if (undo.canUndo()) {
		                    undo.undo();
		                }
		            } catch (CannotUndoException e) {
		            }
		        }
		   });

		newMessageField.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		newMessageField.getActionMap().put("Redo",
		    new AbstractAction("Redo") {
		        public void actionPerformed(ActionEvent evt) {
		            try {
		                if (undo.canRedo()) {
		                    undo.redo();
		                }
		            } catch (CannotRedoException e) {
		            }
		        }
		    });

		newMessageField.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
	}
	

	private void setMessageTextFieldKeyListener() {

		newMessageField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					switch (e.getModifiers())
					{
					case 1://shift
						e.setModifiers(0);
						break;
					case 0://ничего
						e.consume();
						onSendMessage();
						break;
					}
				}
			}

			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyReleased(KeyEvent e) {}
		});
	}
	
	private void onSendMessage()
	{
		if (newMessageField.getText().isEmpty())
		{
			ComponentExtras.Errorize(newMessageField);
			return;
		}
		
		String text = newMessageField.getText().trim();

		ChatMessage message = new ChatMessage(new User(netInfo.currentUser.nickname), text);
		MessagePack msgPack = new MessagePack(chat.users[1], message);
		netInfo.connection.send(msgPack);
		
		addMessage(new ChatMessage(netInfo.currentUser, text));
		
		newMessageField.setText("");
		chats.removeElement(chat);
		chats.add(0, chat);
	}
	
	
	public void addMessage(ChatMessage message)
	{
		chat.messages.add(message);	

		ChatMessagePanel p = new ChatMessagePanel(message);	
		chatListBox.add(p);
		chatListBox.validate();
		
		//без этой строки некорректно рассчитывает высоту последнего элемента
		p.message.getPreferredSize().getHeight();


		Dimension size = chatListBox.getLayout().preferredLayoutSize(chatListBox);
		size.width -= 20;
		chatListBox.setPreferredSize(size);

		scrollToBottom(scrollPanel);
		chat.updateMessageCount();
		
		lastMessagePanel = p;
	}
	
	
	public void update() {

		chatListBox.removeAll();		
		
		for (ChatMessage message : chat.messages) {
			ChatMessagePanel p = new ChatMessagePanel(message);		
			chatListBox.add(p);
			lastMessagePanel = p;
		}
		
		chatListBox.validate();
		
		//аналогично случаю выше
		for (Component message : Arrays.asList(chatListBox.getComponents())) {
			ChatMessagePanel p = (ChatMessagePanel)message;
			p.message.getPreferredSize().getHeight();
		}

		Dimension size = chatListBox.getLayout().preferredLayoutSize(chatListBox);
		size.width -= 20;
		chatListBox.setPreferredSize(size);
		
		chat.updateMessageCount();	
		newMessageField.requestFocus();
	}
	
	
	private static void scrollToBottom(JScrollPane scrollPane) {
	    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
	    AdjustmentListener downScroller = new AdjustmentListener() {
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            Adjustable adjustable = e.getAdjustable();
	            adjustable.setValue(adjustable.getMaximum());
	            verticalBar.removeAdjustmentListener(this);
	        }
	    };
	    verticalBar.addAdjustmentListener(downScroller);
	}
	

	public void setChat(Chat chat) {
		if (this.chat != chat) {
			this.chat = chat;
			if (this.chat != null) {
				titlePanel.title.setText(chat.title());
				titlePanel.avatar.setImage(chat.avatar());
				update();
			}
		}
		
		if (chat == null)
		{
			undo.discardAllEdits();
			newMessageField.setText("");

		}
	}
	
	public Chat getChat() {
		return chat;
	}
	
	public static final Dimension smallAvatarSize = new Dimension(20, 20);
}

class ChatTitlePanel extends JPanel {
	JLabel title = new JLabel();
	ImagePanel avatar = new CircleImagePanel();

	ChatTitlePanel() {
	super(new FlowLayout());
		add(avatar);
		add(title);
		avatar.setPreferredSize(ChatPanel.smallAvatarSize);
	}
}

class ChatMessagePanel extends JPanel {
	private ChatMessage source;

	ChatMessageTitlePanel title = new ChatMessageTitlePanel();
	JTextArea message = new JTextArea();

	ChatMessagePanel(ChatMessage source) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(title);
		add(new JSeparator(JSeparator.HORIZONTAL));
		add(message);

		this.source = source;
		

		title.sender.setText(source.sender.nickname);
		title.datetime.setText(new SimpleDateFormat("HH:mm dd MMMMM yyyy").format(source.date));
		title.avatar.setImage(source.sender.getAvatar().getImage());
		message.setText(source.text);

		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setOpaque(false);
		message.setEditable(false);
		message.setFocusable(true);

		message.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
//		title.setBorder(BorderFactory.createEmptyBorder(0, 0, -5, 0));
		
		setBackground(UIManager.getColor("List.background"));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
	}
	
	@Override
	public int getHeight()
	{
		return title.getHeight() + message.getHeight() + this.getInsets().top + this.getInsets().bottom + 2;
	}
	
	public void update() {
		message.setText(source.text);
	}
}

class ChatMessageTitlePanel extends JPanel {
	JLabel sender = new JLabel();
	JLabel datetime = new JLabel();
	ImagePanel avatar = new CircleImagePanel();

	ChatMessageTitlePanel() {
		super(new FlowLayout(FlowLayout.LEFT));
		add(avatar);
		add(sender);
		add(datetime);
		sender.setFont(new Font("serif", Font.BOLD, 16));
		datetime.setFont(new Font("serif", Font.PLAIN, 12));
		datetime.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		avatar.setPreferredSize(ChatPanel.smallAvatarSize);
	}
}