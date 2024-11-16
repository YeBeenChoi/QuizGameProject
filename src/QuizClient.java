import java.io.*;
import java.net.*;

public class QuizClient {
    private static final String CONFIG_FILE = "server_info.dat"; // 서버 정보 파일 경로
    private static final String DEFAULT_ADDRESS = "localhost"; // 기본 서버 주소
    private static final int DEFAULT_PORT = 1234; // 기본 포트 번호

    public static void main(String[] args) {
        String serverAddress = DEFAULT_ADDRESS;
        int port = DEFAULT_PORT;

        // 서버 정보 파일 읽기
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                serverAddress = reader.readLine(); // 첫 줄: 서버 주소
                port = Integer.parseInt(reader.readLine()); // 두 번째 줄: 포트 번호
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to read server_info.dat. Using default values.");
            }
        }

        // 서버에 연결 시도
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server at " + serverAddress + ":" + port);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION:")) {
                    // 서버로부터 질문 수신
                    System.out.println(serverMessage.substring(9)); // 질문 출력
                    System.out.print("Your answer: ");
                    String answer = console.readLine(); // 사용자 입력
                    out.println("ANSWER:" + answer); // 서버로 응답 전송
                } else if (serverMessage.startsWith("FEEDBACK:")) {
                    // 정답 여부 출력
                    System.out.println(serverMessage.substring(9));
                } else if (serverMessage.startsWith("SCORE:")) {
                    // 최종 점수 출력
                    System.out.println("Final Score: " + serverMessage.substring(6));
                    break; // 퀴즈 종료
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }
}
