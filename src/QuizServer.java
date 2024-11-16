import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234; // 서버가 대기할 포트 번호
    private static final String[][] QUESTIONS = { // 퀴즈 질문과 정답
            {"What is 2 + 2?", "4"},
            {"Capital of France?", "Paris"},
            {"5 x 6?", "30"}
    };

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                try {
                    System.out.println("Waiting for a client...");
                    Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // 클라이언트와 통신을 위한 스트림 생성
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        int score = 0; // 점수 초기화

                        // 질문 전송 및 응답 처리
                        for (String[] qa : QUESTIONS) {
                            out.println("QUESTION:" + qa[0]); // 질문 전송
                            String response = in.readLine(); // 클라이언트 응답 수신

                            if (response != null && response.equalsIgnoreCase("ANSWER:" + qa[1])) {
                                out.println("FEEDBACK:Correct"); // 정답 피드백
                                score++;
                            } else {
                                out.println("FEEDBACK:Incorrect"); // 오답 피드백
                            }
                        }

                        // 최종 점수 전송
                        out.println("SCORE:" + score);
                        System.out.println("Quiz session completed for client.");
                    } catch (IOException e) {
                        System.err.println("Error during communication with client: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
