package kimyongjun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 풀이 시작 : 7:00
 * 풀이 완료 : 7:50
 * 풀이 시간 : 50분
 *
 * 문제 해석
 * 파이어스톰을 2^N * 2^N 격자로 나뉜 얼음판에서 쏨
 * A[r][c] = r행 c열에 있는 얼음의 양
 * 파이어스톰 쓸 때마다 단계 L 결정해야 함
 * 파이어스톰 과정
 * 1. 격자를 2^L * 2^L 크기로 나눔
 * 2. 모든 부분 격자를 시계 방향으로 90도 회전
 * 3. 얼음이 3개 이상 인접하지 않은 모든 칸의 얼음이 1 감소
 *
 * 구해야 하는 것
 * 파이어스톰을 Q번 수행한 후
 * 1. 남아있는 얼음 A[r][c]의 합
 * 2. 남아있는 얼음 중 가장 큰 얼음 덩어리의 크기(인접한 얼음의 크기)
 *
 * 문제 입력
 * 첫째 줄 : N, Q
 * 둘째 줄 ~ 2^N개 줄 : 맵의 초기값
 *
 * 제한 요소
 * 2 <= N <= 6
 * 1 <= Q <= 1000
 * 0 <= A[r][c] <= 100
 * 0 <= L <= N
 *
 * 생각나는 풀이
 * 파이어스톰 수행 과정
 * 1. map을 2^L * 2^L 크기의 판으로 나눠야 함 => 분할 정복처럼 재귀로 반씩 쪼개야 할 듯
 * 2. map 회전 => tempMap[startX + j][startY + L - 1 - i] = map[startX + i][startY + j]
 * 3. 모든 구역에 대해 얼음이 있는 칸 3개 이상 인접했는지 체크 (인접 = 델타 사방탐색)
 *  3-1. 3개 이상 인접했다면 넘어감
 *  3-2. 3개 미만이라면 바로 값을 --시키면 안되고 배열에 체크해놓음 혹은 list에 좌표 저장해놓음
 * 4. 탐색이 끝나고 인접한 얼음이 적은 애들 --시킴
 *
 * 모든 파이어스톰 과정 끝나면
 * 모든 칸 돌면서 얼음 양 더하고
 * 모든 칸 돌면서 dfs or bfs해서 가장 큰 얼음 크기 구하기
 *
 * 구현해야 하는 기능
 * 1. 맵 저장할 배열
 * 2. 인접한 얼음이 없는 좌표를 저장할 리스트
 * 3. 좌표를 나타내는 클래스
 * 4. 파이어스톰 크기만큼 격자를 분할하는 기능
 * 5. 분할된 격자를 오른쪽 90도 회전하는 기능
 * 6. 모든 칸 탐색하며 인접한 얼음의 개수 세는 기능
 *  6-1. 인접한 얼음의 개수가 3개보다 작다면 리스트에 해당 좌표 저장
 * 7. 모든 파이어스톰 반복 끝나고 모든 칸 순회
 *  7-1. 얼음의 양을 전부 더함
 *  7-2. 그래프 탐색으로 가장 큰 얼음 덩어리 칸 수 구함
 */
public class BOJ_20058_마법사상어와파이어스톰 {
    static int N, mapLength;
    static int[][] map; // * 1. 맵 저장할 배열
    static int[][] tempMap; // map 회전할 때 사용하는 임시 배열
    static int[] dx = {-1, 1, 0, 0}; // 사방탐색용 델타배열
    static int[] dy = {0, 0, -1, 1}; // 사방탐색용 델타배열
    static Queue<Point> noIce = new ArrayDeque<>(); // * 2. 인접한 얼음이 없는 좌표를 저장할 리스트

    static class Point { // * 3. 좌표를 나타내는 클래스
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        int Q = Integer.parseInt(st.nextToken());
        mapLength = 1 << N; // 2^N
        map = new int[mapLength][mapLength];
        tempMap = new int[mapLength][mapLength];

        for (int i = 0; i < mapLength; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < mapLength; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        while (Q-- > 0) {
            fireStorm(1 << Integer.parseInt(st.nextToken())); // (1 << L) = 2^L
        }

        System.out.println(findAnswer()); // * 7. 모든 파이어스톰 반복 끝나고 모든 칸 순회
    }

    // 정답 찾는 메서드
    // 모든 파이어스톰 반복 끝나고 모든 칸 순회
    private static String findAnswer() {
        int iceSum = 0; // 남아있는 얼음 총량
        int iceSize = 0; // 남아있는 얼음 덩어리 중 가장 큰 값
        boolean[][] visited = new boolean[mapLength][mapLength];

        for (int i = 0; i < mapLength; i++) {
            for (int j = 0; j < mapLength; j++) {
                if (map[i][j] == 0) continue;
                iceSum += map[i][j];
                if (visited[i][j]) continue;
                iceSize = Math.max(iceSize, bfs(i, j, visited));
            }
        }

        return iceSum + "\n" + iceSize;
    }

    // 얼음 덩어리 찾기 위한 bfs 탐색
    private static int bfs(int startX, int startY, boolean[][] visited) {
        int nowSize = 0;
        Queue<Point> q = new ArrayDeque<>();
        q.offer(new Point(startX, startY));
        visited[startX][startY] = true;

        while (!q.isEmpty()) {
            Point now = q.poll();
            nowSize++;

            for (int i = 0; i < 4; i++) {
                int nextX = now.x + dx[i];
                int nextY = now.y + dy[i];

                if (!isInRange(nextX, nextY) || visited[nextX][nextY] || map[nextX][nextY] == 0) continue;
                visited[nextX][nextY] = true;
                q.offer(new Point(nextX, nextY));
            }
        }

        return nowSize;
    }

    // 파이어스톰 진행하는 메서드
    private static void fireStorm(int L) {
        divide(0, 0, mapLength, L); // * 4. 파이어스톰 크기만큼 격자를 분할하는 기능
        arraySwap(); // tempMap의 주소와 map 주소를 교환
        findIsolatedIce(); // * 6. 모든 칸 탐색하며 인접한 얼음의 개수 세는 기능
        reduceIce();
    }

    // 인접한 얼음이 3개 미만인 좌표 찾는 메서드
    private static void findIsolatedIce() {
        for (int i = 0; i < mapLength; i++) {
            for (int j = 0; j < mapLength; j++) {
                int cnt = 0;
                for (int d = 0; d < 4; d++) {
                    int nextX = i + dx[d];
                    int nextY = j + dy[d];

                    if (!isInRange(nextX, nextY) || map[nextX][nextY] == 0) continue;
                    cnt++;
                }
                // *  6-1. 인접한 얼음의 개수가 3개보다 작다면 리스트에 해당 좌표 저장
                if (cnt <= 2) noIce.offer(new Point(i, j));
            }
        }
    }

    // 격자를 2^L * 2^L 크기까지 분할하는 메서드
    private static void divide(int startX, int startY, int nowLength, int L) {
        if (nowLength == L) {
            rotate(startX, startY, nowLength); // * 5. 분할된 격자를 오른쪽 90도 회전하는 기능
            return;
        }
        int halfLength = nowLength >> 1; // 현재 격자 길이의 절반
        divide(startX, startY, halfLength, L); // 현재 격자의 좌상단
        divide(startX + halfLength, startY, halfLength, L); // 현재 격자의 좌하단
        divide(startX, startY + halfLength, halfLength, L); // 현재 격자의 우상단
        divide(startX + halfLength, startY + halfLength, halfLength, L); // 현재 격자의 우하단
    }

    // 분할된 격자를 시계방향 90도 회전하는 메서드
    private static void rotate(int startX, int startY, int length) {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                tempMap[startX + j][startY + length - 1 - i] = map[startX + i][startY + j];
            }
        }
    }

    // map[][]의 현재 좌표값 1씩 감소시키는 메서드
    private static void reduceIce() {
        while (!noIce.isEmpty()) {
            Point now = noIce.poll();
            if (map[now.x][now.y] == 0) continue;
            map[now.x][now.y]--;
        }
    }

    // 배열 주소값 스왑하는 메서드
    private static void arraySwap() {
        int[][] temp = map;
        map = tempMap;
        tempMap = temp;
    }

    // 배열 범위 내인지 판별하는 메서드
    private static boolean isInRange(int x, int y) {
        return x >= 0 && x < mapLength && y >= 0 && y < mapLength;
    }
}