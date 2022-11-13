import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class ClientTorrent {
	private static final String _Com4 = "PULL_MH";
	private static final String REQUEST = "request";
	private static final String _FileData = "FILE";
	private static final String _Com3 = "PUSH";
	private static final String _Com2 = "PULL";
	private static final String _Com1 = "GET";
	public static int _PORT;
	public static int _PORT_SERVER = 7000;
	public static boolean isFile;
	public static String nazwa;
	public static String path = "D:\\TORrent_";
	public static String _MAIN;
	public static String LIT;
	public static File file;
	public static List<String> tab2;
	public static int PN;
	public static FileInputStream fis;
	public static BufferedInputStream BIS;
	public static Socket s;
	public static OutputStream OS;
	public static ServerSocket serverSocket;
	public static Socket SOCKET;
	public static BufferedOutputStream bos;
	public static InputStream inputStream;
	public static BufferedReader br;
	public static BufferedReader BR;
	public static String C;
	static int _PW;
	static File f;
	static int _SERVER_PORT;
	static List<String> tablicaDate;
	static OutputStream outputStream;
	static File filesList;
	static File[] temp;
	static FileInputStream fileInputStream;

	public ClientTorrent() {
		t1.start();
		ster.start();
	}

	public static void main(String[] args) {
		_PORT = Integer.parseInt(args[0]);
		new ClientTorrent();
		update();
	}

	private static void update() {
		try {
			LIT = "LIST";

			filesList = new File(path + _PORT);

			temp = filesList.listFiles();

			iterate();

			send(new Socket("localhost", _PORT_SERVER), LIT + "\t" + _PORT);
		} catch (Exception exception) {
		}
	}

	private static void iterate() throws IOException, NoSuchAlgorithmException {
		for (File f : temp)
			LIT = LIT + " " + f.getName() + " " + getSum(MessageDigest.getInstance("MD5"), f) + "\n";
	}

	private static String getSum(MessageDigest d, File f) throws IOException {
		fileInputStream = new FileInputStream(f);

		byte[] buff = new byte[1024];
		int coder = 0;

		readModel(d, buff);
		fileInputStream.close();

		byte[] bytes = d.digest();

		StringBuilder sb = new StringBuilder();
		bufforRead(bytes, sb);

		return sb.toString();
	}

	private static void bufforRead(byte[] bytes, StringBuilder sb) {
		for (int i = 0; i < bytes.length; i++)
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	}

	private static void readModel(MessageDigest d, byte[] buff) throws IOException {
		int coder;
		while ((coder = fileInputStream.read(buff)) != -1)
			d.update(buff, 0, coder);
	}

	Thread t1 = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				boolean zawsze_true = true;
				serverSocket = new ServerSocket(_PORT);
				while (zawsze_true) {
					SOCKET = serverSocket.accept();

					if (isFile) {
						openFinal();
						printHello();
					} else {
						BR = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
						String komenda = BR.readLine();
						wykonaj(komenda);
					}
				}
			} catch (Exception exception) {
			}
		}

		private void openFinal() throws IOException, FileNotFoundException {
			byte[] buffor = new byte[8192];
			inputStream = SOCKET.getInputStream();

			bos = new BufferedOutputStream(new FileOutputStream(path + _PORT + "\\" + nazwa));

			int bytesRead = 0;

			for (; (bytesRead = inputStream.read(buffor)) != -1;)
				bos.write(buffor, 0, bytesRead);

			finish();
			isFile = false;
		}

		private void printHello() {
			System.out.println("Pobrano plik " + nazwa);
			System.out.println("Plik nie wymagał retransmisji. ");
		}

		private void finish() throws IOException {
			bos.flush();
			bos.close();
			SOCKET.close();
		}
	});

	Thread ster = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				br = new BufferedReader(new InputStreamReader(System.in));
				do {
					C = br.readLine();
					wykonajPolecenie(C);
				} while (true);
			} catch (Exception exception) {
			}
		}
	});

	protected void wykonajPolecenie(String polecenie) {
		tablicaDate = Arrays.asList(polecenie.split(" "));

		String dyrektywa = tablicaDate.get(0);
		try {
			switch (dyrektywa) {

			case _Com1:
				del1(dyrektywa);

				break;
			case _Com2:
				del2(dyrektywa);

				break;
			case _Com3:
				del3(dyrektywa);

				System.out.println("Wysłano plik " + nazwa);
			case _Com4:
				del4(dyrektywa);
				break;
			case "EXIT":
				System.exit(0);
			}
		} catch (Exception exception) {
		}
	}

	private void del4(String dyrektywa) throws IOException, UnknownHostException {
		send(new Socket("localhost", _PORT_SERVER), dyrektywa + " " + tablicaDate.get(1) + "\t" + _PORT);

		nazwa = tablicaDate.get(1);
		isFile = true;
	}

	private void del3(String dyrektywa)
			throws IOException, UnknownHostException, InterruptedException, FileNotFoundException {
		int portWysylania2 = Integer.parseInt(tablicaDate.get(1));
		send(new Socket("localhost", _PORT_SERVER),
				dyrektywa + " " + portWysylania2 + " " + tablicaDate.get(2) + "\t" + _PORT);

		Thread.sleep(1500);

		f = new File(path + _PORT + "\\" + tablicaDate.get(2));
		FileInputStream fileInputStream = new FileInputStream(f);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		Socket socket5 = new Socket("localhost", portWysylania2);

		outputStream = socket5.getOutputStream();

		byte[] buffor2;
		long rozmiar = f.length();
		long idx = 0;

		while (idx != rozmiar)
			idx = analize(bufferedInputStream, rozmiar, idx);

		outputStream.flush();

		socket5.close();
	}

	private void del2(String dyrektywa) throws IOException, UnknownHostException {
		_PW = Integer.parseInt(tablicaDate.get(1));

		send(new Socket("localhost", _PORT_SERVER),
				dyrektywa + " " + _PW + " " + tablicaDate.get(2) + "\t" + _PORT);

		nazwa = tablicaDate.get(2);
		isFile = true;
	}

	private void del1(String dyrektywa) throws IOException, UnknownHostException {
		_SERVER_PORT = Integer.parseInt(tablicaDate.get(1));
		send(new Socket("localhost", _SERVER_PORT), dyrektywa + "\t" + _PORT);
	}

	private long analize(BufferedInputStream bufferedInputStream, long rozmiar, long idx) throws IOException {
		byte[] bufforPrzesylu;
		int size = 8192;
		if (ifif(rozmiar, idx, size))
			idx += size;
		else {
			size = (int) (rozmiar - idx);
			idx = rozmiar;
		}
		bufforPrzesylu = new byte[size];
		bufferedInputStream.read(bufforPrzesylu, 0, size);
		outputStream.write(bufforPrzesylu);
		return idx;
	}

	private boolean ifif(long rozmiar, long idx, int size) {
		return rozmiar - idx >= size;
	}

	private static void send(Socket socket, String string) throws IOException {
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.write(string);
		finishEnd(socket, pw);
	}

	private static void finishEnd(Socket socket, PrintWriter pw) throws IOException {
		pw.flush();
		pw.close();
		socket.close();
	}

	protected void wykonaj(String komenda) {
		String[] str = komenda.split("\t");
		PN = Integer.parseInt(str[1]);

		tab2 = Arrays.asList(str[0].split(" "));

		_MAIN = tab2.get(0);
		try {
			if ("GET_RESPONSE".equals(_MAIN))
				for (int i = 1; i < tab2.size(); i++)
					System.out.print(tab2.get(i) + " ");

			else if (_FileData.equals(_MAIN)) {
				nazwa = tab2.get(1);
				isFile = true;
			}

			else if (REQUEST.equals(_MAIN)) {
				Thread.sleep(1000);

				init();

				OS = s.getOutputStream();

				byte[] bufv;
				long size = file.length();
				long idx = 0;

				for(;idx != size;)
					idx = dataBuf(size, idx);

				OS.flush();

				s.close();
			}
		} catch (Exception exception) {
		}
	}

	private void init() throws FileNotFoundException, UnknownHostException, IOException {
		file = new File(path + _PORT + "\\" + tab2.get(1));
		fis = new FileInputStream(file);
		BIS = new BufferedInputStream(fis);
		s = new Socket("localhost", PN);
	}

	private long dataBuf(long rozmiar, long idx) throws IOException {
		byte[] bufforPrzesylu;
		int size = 8192;
		if (ifif(rozmiar, idx, size))
			idx += size;
		else {
			size = (int) (rozmiar - idx);
			idx = rozmiar;
		}
		bufforPrzesylu = new byte[size];
		BIS.read(bufforPrzesylu, 0, size);
		OS.write(bufforPrzesylu);
		return idx;
	}
}
