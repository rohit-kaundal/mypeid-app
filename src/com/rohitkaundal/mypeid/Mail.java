package com.rohitkaundal.mypeid;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import android.util.Log;

import com.sun.mail.imap.IMAPFolder;

@SuppressWarnings("serial")
public class Mail extends javax.mail.Authenticator implements Serializable {
	private String _user;
	private String _pass;

	private String[] _to;
	private String[] _cc = null;
	private String[] _bcc = null;

	private String _from;

	private String _port;
	private String _sport;

	private String _host;

	private String _subject;
	private String _body;

	private boolean _auth;

	private boolean _debuggable;

	private Multipart _multipart;

	public Mail() {
		_host = "mypeid.com"; // default smtp server
		_port = "25"; // default smtp port
		_sport = "25"; // default socketfactory port

		_user = ""; // username
		_pass = ""; // password
		_from = ""; // email sent from
		_subject = ""; // email subject
		_body = ""; // email body

		_debuggable = true; // debug mode on or off - default off
		_auth = true; // smtp authentication - default on

		_multipart = new MimeMultipart();

		// There is something wrong with MailCap, javamail can not find a
		// handler for the multipart/mixed part, so this bit needs to be added.
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
				.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public Mail(String user, String pass) {
		this();

		_user = user;
		_pass = pass;
	}

	public boolean send() throws Exception {
		Properties props = _setProperties();

		if (!_user.equals("") && !_pass.equals("") && _to.length > 0
				&& !_from.equals("") && !_subject.equals("")
				&& !_body.equals("")) {
			Session session = Session.getInstance(props, this);

			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(_from));

			InternetAddress[] addressTo = new InternetAddress[_to.length];
			for (int i = 0; i < _to.length; i++) {
				addressTo[i] = new InternetAddress(_to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			if (_cc != null && _cc.length > 0) {
				InternetAddress[] addressCC = new InternetAddress[_cc.length];
				for (int i = 0; i < _to.length; i++) {
					addressCC[i] = new InternetAddress(_cc[i]);
				}
				msg.setRecipients(MimeMessage.RecipientType.CC, addressCC);
			}

			if (_bcc != null && _bcc.length > 0) {
				InternetAddress[] addressBCC = new InternetAddress[_bcc.length];
				for (int i = 0; i < _to.length; i++) {
					addressBCC[i] = new InternetAddress(_bcc[i]);
				}
				msg.setRecipients(MimeMessage.RecipientType.BCC, addressBCC);
			}
			msg.setSubject(_subject);
			msg.setSentDate(new Date());

			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			
			messageBodyPart.setText(_body);
			messageBodyPart.setContent(_body,"text/html");
			_multipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(_multipart);

			// send email
			Transport.send(msg);

			// Copy message to "Sent Items" folder as read
			Store store = session.getStore("imaps");
			store.connect(_host, _user, _pass);

			Folder folder = (Folder) store.getFolder("Sent");
			if (!folder.exists()) {
				folder.create(Folder.HOLDS_MESSAGES);
			}
			folder.open(Folder.READ_WRITE);
			System.out.println("appending...");
			// folder.appendMessages(new Message[]{message});
			try {

				folder.appendMessages(new Message[] { msg });
				// Message[] msgs = folder.getMessages();
				msg.setFlag( Flags.Flag.RECENT, true);

			} catch (Exception ignore) {
				System.out.println("error processing message "
						+ ignore.getMessage());
			} finally {
				store.close();
				//folder.close(true);
			}

			return true;
		} else {
			return false;
		}
	}

	public void addAttachment(String filename) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);

		_multipart.addBodyPart(messageBodyPart);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(_user, _pass);
	}

	private Properties _setProperties() {
		Properties props = new Properties();

		props.put("mail.smtp.host", _host);

		if (_debuggable) {
			props.put("mail.debug", "true");
		}

		if (_auth) {
			props.put("mail.smtp.auth", "true");
		}

		props.put("mail.smtp.port", _port);
		props.put("mail.smtp.socketFactory.port", _sport);
		// props.put("mail.smtp.ssl.enable", "false");
		// props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.socketFactory.class","");
		props.put("mail.smtp.socketFactory.fallback", "false");
		// props.put("mail.smtp.starttls.enable", "false");
		// I tried this by itself and also together with ssl.enable)
		// props.put("mail.smtp.ssl.enable", "true");
		
		
		props.setProperty("mail.store.protocol", "imaps");
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// System.setProperty("javax.net.debug", "ssl,handshake");
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");
		
		

		return props;
	}

	// the getters and setters
	public String getBody() {
		return _body;
	}

	public void setBody(String _body) {
		this._body = _body;
	}

	public void setTo(String[] toArr) {
		this._to = toArr;
	}

	public void setCC(String[] ccArr) {
		this._cc = ccArr;
	}

	public void setBCC(String[] bccArr) {
		this._bcc = bccArr;
	}

	public void setFrom(String string) {
		this._from = string;
	}

	public void setSubject(String string) {
		this._subject = string;
	}

	// more of the getters and setters …..
	public boolean authenticate() {

		Properties props = System.getProperties();

		props.setProperty("mail.store.protocol", "imaps");
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// System.setProperty("javax.net.debug", "ssl,handshake");
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");

		/*
		 * Properties props = System.getProperties();
		 * props.setProperty("mail.transport.protocol", "smtp"); String
		 * SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; //
		 * System.setProperty("javax.net.debug", "ssl,handshake");
		 * props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		 * //props.setProperty("mail.imap.socketFactory.fallback", "false");
		 * props.setProperty("mail.smtp.port", "25");
		 * props.setProperty("mail.smtp.socketFactory.port", "25");
		 * //props.setProperty("mail.store.protocol", "imaps");
		 */

		Session session = Session.getDefaultInstance(props, null);

		javax.mail.Store store;
		try {
			store = session.getStore("imaps");
			store.connect("mypeid.com", this.getUser(), this.getPass());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		/*
		 * if(!_user.equals("") && !_pass.equals("")) {
		 * 
		 * try{
		 * 
		 * //Security.addProvider(new Provider()); String SSL_FACTORY =
		 * "javax.net.ssl.SSLSocketFactory";
		 * //System.setProperty("javax.net.debug", "ssl,handshake");
		 * props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		 * props.setProperty("mail.imap.socketFactory.fallback", "false");
		 * props.setProperty("mail.imap.port", "993");
		 * props.setProperty("mail.imap.socketFactory.port", "993"); //
		 * props.put("mail.imap.auth.plain.disable","true");
		 * 
		 * Session session = null; //props = new Properties(); session =
		 * Session.getInstance(props, null); //session =
		 * Session.getDefaultInstance(props, null); session.setDebug(true);
		 * 
		 * javax.mail.Store store;
		 * 
		 * 
		 * store = session.getStore("imap");
		 * store.connect(this._host,993,this._user,this._pass); return "Awsome";
		 * }catch (Exception e) { return e.toString(); }
		 * 
		 * 
		 * } else { return new String("empty uname and pwd"); }
		 */
	}

	public String getUser() {
		// TODO Auto-generated method stub
		return this._user;
	}

	public String getPass() {
		// TODO Auto-generated method stub
		return this._pass;
	}

	public ArrayList<MessageHolder> getMailsFromInbox()
			throws MessagingException, Exception {

		javax.mail.Store store = null;
		IMAPFolder folder = null;
		ArrayList<MessageHolder> msg_list = new ArrayList<MessageHolder>();

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// System.setProperty("javax.net.debug", "ssl,handshake");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);

		store = session.getStore("imaps");
		store.connect("mypeid.com", this.getUser(), this.getPass());

		folder = (IMAPFolder) store.getFolder("inbox"); // This doesn't work for
														// other email account
		// folder = (IMAPFolder) store.getFolder("inbox"); This works for both
		// email account

		if (!folder.isOpen())
			folder.open(Folder.READ_WRITE);

		Message messages[] = folder.getMessages();
		String result = "";

		for (int i = 0; i < messages.length; i++) {
			result = "";

			Object obj = messages[i].getContent();
			if (obj instanceof String) {
				result = obj.toString();

			} else {

				MimeMultipart mimeMultipart = (MimeMultipart) messages[i]
						.getContent();
				int count = mimeMultipart.getCount();
				for (int j = 0; j < count; j++) {
					BodyPart bodyPart = mimeMultipart.getBodyPart(j);

					if (bodyPart.isMimeType("text/plain")) {
						result = result + "\n"
								+ bodyPart.getContent().toString();
						 //without break same text appears twice in my
						// tests
					} else if (bodyPart.isMimeType("text/html")) {
						String html = (String) bodyPart.getContent().toString();
						result = html;
						

					} else {
						result += "\nUknown";
					}
				}
			}

			MessageHolder msgholder = new MessageHolder(
					messages[i].getMessageNumber(),
					messages[i].getFrom()[0].toString(),
					messages[i].getAllRecipients()[0].toString(),
					messages[i].getSubject(), result, messages[i]
							.getReceivedDate().toString(), "inbox");
			msg_list.add(msgholder);
		}

		return msg_list;

	}

	public ArrayList<MessageHolder> getMailsFromFolder(String strFolderName)
			throws MessagingException, Exception {

		javax.mail.Store store = null;
		IMAPFolder folder = null;
		ArrayList<MessageHolder> msg_list = new ArrayList<MessageHolder>();

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// System.setProperty("javax.net.debug", "ssl,handshake");
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);

		store = session.getStore("imaps");
		store.connect("mypeid.com", this.getUser(), this.getPass());

		folder = (IMAPFolder) store.getFolder(strFolderName); // This doesn't
																// work for
		// other email account
		// folder = (IMAPFolder) store.getFolder("inbox"); This works for both
		// email account

		if (!folder.isOpen())
			folder.open(Folder.READ_WRITE);

		Message messages[] = folder.getMessages();
		String result = "";

		for (int i = 0; i < messages.length; i++) {
			result = "";

			Object obj = messages[i].getContent();
			if (obj instanceof String) {
				result = obj.toString();

			} else {

				MimeMultipart mimeMultipart = (MimeMultipart) messages[i]
						.getContent();
				int count = mimeMultipart.getCount();
				for (int j = 0; j < count; j++) {
					BodyPart bodyPart = mimeMultipart.getBodyPart(j);

					if (bodyPart.isMimeType("text/plain")) {
						result = result + "\n"
								+ bodyPart.getContent().toString();
						  //without break same text appears twice in my
						// tests
					} else if (bodyPart.isMimeType("text/html")) {
						String html = (String) bodyPart.getContent().toString();
						result = html;
						

					} else {
						result += "\nUknown";
					}
				}
			}

			MessageHolder msgholder = new MessageHolder(
					messages[i].getMessageNumber(),
					messages[i].getFrom()[0].toString(),
					messages[i].getAllRecipients()[0].toString(),
					messages[i].getSubject(), result, messages[i]
							.getReceivedDate().toString(), strFolderName);
			msg_list.add(msgholder);
		}

		return msg_list;

	}

	public List<File> getAttachments(Message message) {

		return null;

	}

	public boolean boolDeleteMail(int iMsgNumber, String strFolderName) {

		try {

			javax.mail.Store store = null;
			IMAPFolder folder = null;
			ArrayList<MessageHolder> msg_list = new ArrayList<MessageHolder>();

			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			// System.setProperty("javax.net.debug", "ssl,handshake");
			props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.imap.socketFactory.fallback", "false");
			props.setProperty("mail.imap.port", "993");
			props.setProperty("mail.imap.socketFactory.port", "993");
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);

			store = session.getStore("imaps");
			store.connect("mypeid.com", this.getUser(), this.getPass());

			folder = (IMAPFolder) store.getFolder(strFolderName); // This
																	// doesn't
																	// work for
			// other email account
			// folder = (IMAPFolder) store.getFolder("inbox"); This works for
			// both
			// email account

			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);

			Message messages = folder.getMessage(iMsgNumber);

			if (messages != null) {
				messages.setFlag(Flags.Flag.DELETED, true);

			}

			folder.close(true);
			store.close();
			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public boolean boolArchiveMail(int iMsgNumber, String strFolderName) {

		try {

			javax.mail.Store store = null;
			IMAPFolder folder = null;
			ArrayList<MessageHolder> msg_list = new ArrayList<MessageHolder>();

			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			// System.setProperty("javax.net.debug", "ssl,handshake");
			props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.imap.socketFactory.fallback", "false");
			props.setProperty("mail.imap.port", "993");
			props.setProperty("mail.imap.socketFactory.port", "993");
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);

			store = session.getStore("imaps");
			store.connect("mypeid.com", this.getUser(), this.getPass());

			folder = (IMAPFolder) store.getFolder(strFolderName); // This
																	// doesn't
																	// work for
			// other email account
			// folder = (IMAPFolder) store.getFolder("inbox"); This works for
			// both
			// email account

			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);

			Message messages = folder.getMessage(iMsgNumber);

			if (messages != null) {
				messages.setFlag(Flags.Flag.FLAGGED, true);

			}

			folder.close(true);
			store.close();
			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public ArrayList<MailBoxFolder> getFolderList() {
		javax.mail.Store store = null;
		IMAPFolder folder = null;
		ArrayList<MailBoxFolder> msg_list = new ArrayList<MailBoxFolder>();

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// System.setProperty("javax.net.debug", "ssl,handshake");
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);

		try {
			store = session.getStore("imaps");
			store.connect("mypeid.com", this.getUser(), this.getPass());

			Folder[] flist = store.getDefaultFolder().list();
			for (Folder fd : flist) {
				MailBoxFolder tmpfolder = new MailBoxFolder();
				tmpfolder.set_title(fd.getName());
				tmpfolder.set_count(fd.getMessageCount());
				tmpfolder.set_fullname(fd.getFullName());
				msg_list.add(tmpfolder);
			}
			return msg_list;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public boolean boolSaveMessageAsDraft() throws AddressException,
			MessagingException {
		Properties props = _setProperties();

		if (!_user.equals("") && !_pass.equals("") && _to.length > 0
				&& !_from.equals("") && !_subject.equals("")
				&& !_body.equals("")) {
			Session session = Session.getInstance(props, this);

			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(_from));

			InternetAddress[] addressTo = new InternetAddress[_to.length];
			for (int i = 0; i < _to.length; i++) {
				addressTo[i] = new InternetAddress(_to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			InternetAddress[] addressCC = new InternetAddress[_cc.length];
			for (int i = 0; i < _to.length; i++) {
				addressCC[i] = new InternetAddress(_cc[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.CC, addressCC);

			InternetAddress[] addressBCC = new InternetAddress[_bcc.length];
			for (int i = 0; i < _to.length; i++) {
				addressBCC[i] = new InternetAddress(_bcc[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.BCC, addressBCC);

			msg.setSubject(_subject);
			msg.setSentDate(new Date());

			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(_body);
			_multipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(_multipart);

			// send email
			Transport.send(msg);

			return true;
		} else {
			return false;
		}

	}
}