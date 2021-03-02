package Views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import ClientServer.Chat;
import ClientServer.ChatMessage;
import Components.CircleImagePanel;
import Components.ImagePanel;
import Components.TextPanel;


//Панель мессенджера (список чатов)
public class ChatListPanel extends JPanel {

	public DefaultListModel<Chat> chats = new DefaultListModel<Chat>();
	private JList<Chat> chatList = new JList<>();
	

	public ChatListPanel() {
		super(new BorderLayout());
		JScrollPane scrollPanel = new JScrollPane(chatList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setBorder(null);
		add(scrollPanel);
		
		chatList.setModel(chats);
		chatList.setCellRenderer(new CellRenderer());
		chatList.addMouseMotionListener(mouseMotionListener);
		chatList.addMouseListener(mouseListener);
		chatList.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}


	public void update() {
		chatList.updateUI();
	}
	
	
	public Consumer<Chat> itemDoubleClickEventHandler = (chat) -> {};
	
	
	MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			int index = chatList.locationToIndex(e.getPoint());
			if (index != -1)
			{
				Chat value = chatList.getModel().getElementAt(index);
				Component comp = chatList.getCellRenderer().getListCellRendererComponent(chatList, value, index, false, true);
				comp.requestFocus();comp.repaint();
				chatList.updateUI();chatList.setSelectedIndex(index);
			}

		}
	};
	
	MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseExited(MouseEvent arg0) {
			chatList.clearSelection();
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JList<?> list = (JList<?>) e.getSource();
			// двойной щелчек левой кнопкой мыши
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0)
					itemDoubleClickEventHandler.accept(chats.get(index));
			} 
		}
		
		@Override public void mouseEntered(MouseEvent arg0) {}
		@Override public void mousePressed(MouseEvent arg0) {}
		@Override public void mouseReleased(MouseEvent arg0) {}

	};
	
	
	private static final Dimension smallAvatarSize = new Dimension(25, 25);
	private static final Dimension bigAvatarSize = new Dimension(55, 55);

	 //Класс, отвечающий за рендеринг элемента JLIST
class CellRenderer extends JPanel implements ListCellRenderer<Chat> {

		private static final long serialVersionUID = -4742471097902593363L;
		private JPanel p = new JPanel(new BorderLayout());
		
		private JLabel title = new JLabel();
		private JLabel readMessageCount = new JLabel(); // всего сообщений
		private JLabel newMessageCount = new JLabel(); // новых сообщений
		private TextPanel lastMessageLabel = new TextPanel(true);
		private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		private JPanel lastMessagePanel = new JPanel(new GridBagLayout());
		private ImagePanel lastMessageSenderAvatar = new CircleImagePanel();
		private ImagePanel chatAvatar = new CircleImagePanel();
		
	
		public CellRenderer() {
			super(new BorderLayout());		
			setLayout();
			setDecoration();
		}
		
		
		private void setLayout()
		{
			add(p);
			
			GridBagConstraints constraints =  new GridBagConstraints();
			constraints.insets = new Insets(2, 3, 3, 0);
			lastMessagePanel.add(lastMessageSenderAvatar, constraints);		
			constraints.insets = new Insets(0, 5, 1, 0);
			constraints.anchor = GridBagConstraints.NORTH; 
			constraints.fill   = GridBagConstraints.BOTH;  
			constraints.gridheight = 1;
			constraints.gridwidth  = GridBagConstraints.REMAINDER; 
			constraints.gridx = GridBagConstraints.RELATIVE; 
			constraints.gridy = GridBagConstraints.RELATIVE; 
			constraints.weightx = 1.0;			
			lastMessagePanel.add(lastMessageLabel, constraints);
			lastMessageSenderAvatar.setPreferredSize(smallAvatarSize);
			
	
			titlePanel.add(title);
			titlePanel.add(new JLabel(":"));
			titlePanel.add(readMessageCount);
			titlePanel.add(newMessageCount);
			
			p.add(titlePanel, BorderLayout.PAGE_START);
			p.add(new JSeparator(JSeparator.HORIZONTAL));
			p.add(lastMessagePanel, BorderLayout.PAGE_END);
			
		
			add(chatAvatar, BorderLayout.LINE_START);
			chatAvatar.setPreferredSize(bigAvatarSize);		
		}

		private void setDecoration()
		{
			Border outsideBorder = BorderFactory.createEmptyBorder(5, 3, 5, 3);
			Border insideBorder = BorderFactory.createEtchedBorder();
			Border border = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
			setBorder(border);
			
			titlePanel.setBorder(BorderFactory.createEmptyBorder(-2, -2, -2, -2));
			lastMessageLabel.setFont(lastMessageLabel.getFont().deriveFont(Font.PLAIN));
			newMessageCount.setForeground(Color.red);
			title.setFont(new Font("Verdana", Font.BOLD, 12));	
		}
		
		private final Color focusColor = new Color(117,187,253);
		
		@Override
		public Component getListCellRendererComponent(JList<? extends Chat> list, Chat chat, int index,
				boolean isSelected, boolean cellHasFocus) {

			title.setText(chat.title());
			
			readMessageCount.setText(Integer.toString(chat.getMessageCount()));
			
			int newMesssageCount = chat.messages.size() - chat.getMessageCount();
			newMessageCount.setText((newMesssageCount > 0) ? "+" + newMesssageCount : "");
			
			ChatMessage lastMsg = chat.messages.isEmpty() ? null : chat.messages.get(chat.messages.size() - 1);
			lastMessageLabel.setText(lastMsg == null ? "" : lastMsg.text);

			lastMessageSenderAvatar.setImage(lastMsg == null ? ImagePanel.emptyImage : lastMsg.sender.getAvatar().getImage());

			chatAvatar.setImage(chat.users[1].getAvatar().getImage());

			
			Color color = newMesssageCount > 0 ? list.getSelectionBackground() : list.getBackground();
			if (isSelected) color = focusColor;
			setBackground(color);
			return this;
		}
		
		@Override
		public void setBackground(Color color)
		{
			try
			{
				super.setBackground(color);
				p.setBackground(color);
				titlePanel.setBackground(color);
				chatAvatar.setBackground(color);
				lastMessageLabel.setBackground(color);
				lastMessagePanel.setBackground(color);
				lastMessageSenderAvatar.setBackground(color);
			}
			catch(NullPointerException e)
			{
//				e.printStackTrace();
			}
		}
	}

}