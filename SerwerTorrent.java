import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SerwerTorrent {
	private static final String _Com4 = "PULL_MH";
	private static final String _Com3 = "PULL";
	private static final String _Com2 = "GET";
	private static final String _Com1 = "LIST";

	public class F {
		public String NAME;
		public String CODER;

		public F(String str, String str2) {
			this.NAME = str;
			this.CODER = str2;
		}

		public String toString() {
			return this.NAME + " MD5 : " + this.CODER;
		}
	}
	public static int pn;
	public static int p = 1;
	public static String[] tab;
	public static List<String> L;
	public static ArrayList<F> tor;
	public static ServerSocket server;
	public static int port;
	public static Scanner scanner;
	public static PrintWriter pw;
	public static String MAIN;
	

	HashMap<String, ArrayList<F>> torrentOrg = new HashMap<>();

	public SerwerTorrent() {
		thread.start();
	}

	public static void main(String[] args) {
		port = Integer.parseInt(args[0]);

		new SerwerTorrent();
	}

	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				boolean zawsze_true = true;
				server = new ServerSocket(port);

				readMoreInf(zawsze_true);
			} catch (Exception exception) {}
		}

		private void readMoreInf(boolean zawsze_true) throws IOException {
			for (; zawsze_true;) {
				scanner = new Scanner(server.accept().getInputStream());

				MAIN = "";
				for (; scanner.hasNextLine();)
					MAIN = MAIN + scanner.nextLine();

				placeGround(MAIN);
				scanner.close();
			}
		}
	});

	protected void placeGround(String comm) {
		doItSelf(comm);

		String dyrektywa = L.get(0);
		try {
			switch (dyrektywa) {

			case _Com1:

				torrentOrg.remove(pn);
				ArrayList<F> lista = new ArrayList<>();
				iterate(lista);

				torrentOrg.put(String.valueOf(pn), lista);

				System.out.println(torrentOrg);
				break;

			case _Com2:
				send(new Socket("localhost", pn), "GET_RESPONSE" + " " + torrentOrg + "\t" + port);

				break;
			case _Com3:
				System.out.println("Klient " + pn + " pobiera plik: " + L.get(2) + " od " + L.get(p()));
				send(new Socket("localhost", Integer.parseInt(L.get(p()))), "request" + " " + L.get(2) + "\t" + pn);

				break;
			case _Com4:
				String[] users = torrentOrg.keySet().toArray(new String[torrentOrg.size()]);
				String list = "";
				list = iterateOverDate(users, list);
				com1(list);
				break;
			case "PUSH":
				com2();

				send(new Socket("localhost", Integer.parseInt(L.get(p()))), "FILE" + " " + L.get(2) + "\t" + pn);

				break;
			}
		} catch (Exception exception) {
		}
	}

	private void doItSelf(String str) {
		tab = str.split("\t");
		pn = Integer.parseInt(tab[p()]);

		L = Arrays.asList(tab[0].split(" "));
	}

	private void iterate(ArrayList<F> lista) {
		for (int i = p(); i < L.size(); i++) {
			F p = new F(L.get(i), L.get(++i));
			lista.add(p);
		}
	}

	private String iterateOverDate(String[] users, String list) throws IOException, UnknownHostException {
		for (int i = 0; i < users.length; i++) {
			tor = torrentOrg.get(users[i]);
			list = iterateTor(users, list, i);
		}
		return list;
	}

	private String iterateTor(String[] users, String list, int i) throws IOException, UnknownHostException {
		for (F p : tor) {
			if (p.NAME.equals(L.get(p()))) {
				list += "klient " + users[i] + ", ";

				send(new Socket("localhost", Integer.parseInt(users[i])),
						"request" + " " + L.get(p()) + "\t" + pn);

				break;
			}
		}
		return list;
	}

	private void com2() {
		System.out.println("Klient " + pn + " wysyła plik: " + L.get(2) + " do " + L.get(p()));
	}

	private void com1(String list) {
		System.out.println("Klient " + pn + " pobiera plik: " + L.get(p()) + " od " + list);
	}

	private void send(Socket soc, String message) throws IOException {
		pw = new PrintWriter(soc.getOutputStream());
		pw.write(message);
		finish(soc);
	}

	private void finish(Socket soc) throws IOException {
		pw.flush();
		pw.close();
		soc.close();
	}

	public static int p() {
		return p;
	}
}
