package kimyongjun;
/**
 * 풀이 시작 : 12:43
 * 풀이 완료 : 14:33
 * 풀이 시간 : 110분
 *
 * 문제 해석
 * N * N 크기의 맵에서 토네이도를 발생시킴
 * A[r][c] = r행 c열의 모래의 양
 * 토네이도 이동 경로는 가운데 칸에서 시작해 반시계방향으로 회전하며 밖으로 이동
 * 한 칸 이동할 때 목적지의 모든 모래는 정해진 비율에 따라 주위로 흩어짐
 * 모래가 이미 있는 칸으로 모래가 이동하면 기존 양에 더해짐
 * 토네이도는 (1,1)까지 이동 후 소멸
 *
 * 구해야 하는 것
 * 밖으로 나간 모래의 양을 구해야 함
 *
 * 문제 입력
 * 첫째 줄 : 맵의 크기 N
 * 둘째 줄 ~ N개 줄 : 각 위치의 초기 모래 양
 *
 * 제한 요소
 * 3 <= N <= 499
 * N % 2 == 1
 * 0 <= A[r][c] <= 1000
 * 시작 지점(가운데 칸)의 모래 양은 0
 *
 * 생각나는 풀이
 * 시뮬레이션
 * 움직임 구현 => 맵 크기의 visit 배열 생성, 이동 후 나의 왼쪽 90도 방향이 방문하지 않은 칸이라면 방향을 변경
 * 모래 이동 구현 => 이동 방향 기준으로 날아가는 모래가 다름
 *  - 왼쪽으로 이동시
 *      - 목표 칸 오른쪽, 왼쪽 90도 1칸 = 7%
 *      - 목표 칸의 오른쪽, 왼쪽 90도 2칸 = 2%
 *      - 목표 칸의 전방 1칸 오른쪽, 왼쪽 90도 1칸 = 10%
 *      - 목표 칸의 전방 2칸 - 5%
 *      - 목표 칸의 후방 1칸 오른쪽, 왼쪽 90도 1칸 - 1%
 *      - 목표 칸의 전방 1칸 - 나머지
 * 구현은?? 현재 방향(델타 배열) 이용해서 계산하면 될 것 같음
 *  ex) 오른쪽 90도 1칸 = (now.x + dx[dir - 1] * 1, now.y + dy[dir - 1] * 1)
 *      왼쪽 90도 2칸 = (now.x + dx[dir + 1] * 2, now.y + dy[dir + 1] * 2)
 * => 계산 후에는 각 칸이 배열 범위 벗어났다면 해당되는 모래 양을 답을 저장하는 변수에 더해줌
 * 1,1까지 이동했다면 종료
 *
 * 구현해야 하는 기능
 * 1. 맵을 저장할 배열
 * 2. 방문 처리할 배열
 * 3. 토네이도의 위치, 방향 저장할 클래스
 * 4. 이동 프로세스 구현
 *  4-1. 초기 이동 방향은 왼쪽
 *  4-2. 방문처리 후 현재 방향으로 이동
 *  4-3. 모래의 이동 과정 실행
 *  4-3. 현재 방향의 왼쪽 한 칸이 방문하지 않았다면 방향 전환
 * 5. 위의 과정을 (0, 0)까지 이동 후 종료
 *
 * 모래의 이동 과정
 * 1. 현재 위치의 모래 양을 저장
 * 2. 모래가 이동할 위치 계산
 *  현재 칸이 (x, y), 방향은 dir, dx[좌, 하, 우, 상], dy[좌, 하, 우, 상] 순
 *  2-1. 왼쪽 한 칸 = (x + dx[(4 + dir + 1) % 4], y + dy[(4 + dir + 1) % 4])
 *  2-2. 오른쪽 한 칸 = (x + dx[(4 + dir - 1) % 4], y + dy[(4 + dir - 1) % 4])
 *  2-3. 왼쪽 2칸 = (x + 2 * dx[(4 + dir + 1) % 4], y + 2 * dy[(4 + dir + 1) % 4])
 *  2-4. 오른쪽 2칸 = (x + 2 * dx[(4 + dir - 1) % 4], y + 2 * dy[(4 + dir - 1) % 4])
 *  2-5. 전방 1칸, 왼쪽 1칸 = (x + dx[dir] + dx[(4 + dir + 1) % 4], y + dy[dir] + dy[(4 + dir + 1) % 4])
 *  2-6. 전방 1칸, 오른쪽 1칸 = (x + dx[dir] + dx[(4 + dir - 1) % 4], y + dy[dir] + dy[(4 + dir - 1) % 4])
 *  2-7. 전방 2칸 = (x + 2 * dx[dir], y + 2 * dy[dir])
 *  2-8. 후방 1칸, 왼쪽 1칸 = (x - dx[dir] + dx[(4 + dir + 1) % 4], y - dy[dir] + dy[(4 + dir + 1) % 4])
 *  2-9. 후방 1칸, 오른쪽 1칸 = (x - dx[dir] + dx[(4 + dir - 1) % 4], y - dy[dir] + dy[(4 + dir - 1) % 4])
 *  2-10. 전방 1칸 = (x + dx[dir], y + dy[dir])
 * 3. 모래가 이동할 위치에 지정된 양만큼 현재 모래에서 빼서 이동
 *  3-1. 만약 이동할 위치가 배열 범위 밖이라면 답에 더해주고 continue
 *  3-2. 그렇지 않다면 기존 모래 양에 더해줌
 *
 * ------기능 수정--------
 * 델타에 따라서 계산하려면 더 복잡해지는듯
 * 그냥 4방향에 따라 전부 예상 지점 전처리해서 지정하는게 나은 거 같음
 * direction[dir][모래 흩날리는 방향 10개]
 * 반복을 9번만 처리하고 마지막 direction[dir][9]는 다 날리고 나머지 가져가는 알파용
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class BOJ_20057_마법사상어와토네이도 {
    static int N, sum;
    static Tornado tornado;
    static int[][] map; // * 1. 맵을 저장할 배열
    static boolean[][] visited; // * 2. 방문 처리할 배열
    static int[] dx = {0, 1, 0, -1}; // 좌, 하, 우, 상
    static int[] dy = {-1, 0, 1, 0}; // 좌, 하, 우, 상
    static int[][] directionX = { // 모래가 이동할 위치를 전처리
            {-1, 1, -2, -1, 1, 2, -1, 1, 0, 0}, // 좌
            {-1, -1, 0, 0, 0, 0, 1, 1, 2, 1}, // 하
            {-1, 1, -2, -1, 1, 2, -1, 1, 0, 0}, // 우
            {1, 1, 0, 0, 0, 0, -1, -1, -2, -1} // 상
    };
    static int[][] directionY = { // 모래가 이동할 위치를 전처리
            {1, 1, 0, 0, 0, 0, -1, -1, -2, -1}, // 좌
            {-1, 1, -2, -1, 1, 2, -1, 1, 0, 0}, // 하
            {-1, -1, 0, 0, 0, 0, 1, 1, 2, 1}, // 우
            {-1, 1, -2, -1, 1, 2, -1, 1, 0, 0} // 상
    };
    static int[] ratio = {1, 1, 2, 7, 7, 2, 10, 10, 5};

    static class Tornado { // * 3. 토네이도의 위치, 방향 저장할 클래스
        int x, y, dir;

        public Tornado(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        StringTokenizer st;

        map = new int[N][N];
        visited = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        tornado = new Tornado(N / 2, N / 2, 0); // *  4-1. 초기 이동 방향은 왼쪽
        simulation();
        System.out.println(sum);

    }

    private static void simulation() {
        // * 4. 이동 프로세스 구현
        while (tornado.x != 0 || tornado.y != 0) { // * 5. 위의 과정을 (0, 0)까지 이동 후 종료
            visited[tornado.x][tornado.y] = true; // 방문 처리
            moveTornado(); // *  4-2. 현재 방향으로 이동
            moveSand(); // *  4-3. 모래의 이동 과정 실행
            checkDir(); // *  4-3. 현재 방향의 왼쪽 한 칸이 방문하지 않았다면 방향 전환
        }

    }

    // 토네이도 이동
    private static void moveTornado() {
        tornado.x += dx[tornado.dir];
        tornado.y += dy[tornado.dir];
    }

    // 모래 이동
    private static void moveSand() {
        int x = tornado.x;
        int y = tornado.y;
        if (map[x][y] == 0) return;
        int dir = tornado.dir;
        int sand = map[x][y]; // * 1. 현재 위치의 모래 양을 저장

        // * 2. 모래가 이동할 위치 계산
        for (int i = 0; i < 9; i++) {
            // * 3. 모래가 이동할 위치에 지정된 양만큼 현재 모래에서 빼서 이동
            int nowValue = (map[x][y] * ratio[i]) / 100;
            sand -= nowValue;
            int nextX = x + directionX[dir][i], nextY = y + directionY[dir][i];
            if (isIrRange(nextX, nextY)) {
                map[nextX][nextY] += nowValue; // *  3-2. 배열 안이라면 기존 모래 양에 더해줌
            } else sum += nowValue; // *  3-1. 만약 이동할 위치가 배열 범위 밖이라면 답에 더해주고
        }

        if (isIrRange(x + dx[dir], y + dy[dir])) {
            map[x + dx[dir]][y + dy[dir]] += sand;
        } else sum += sand;

        map[x][y] = 0; // 현재 칸 남은 모래는 0
    }

    // 배열 범위 체크 메서드
    private static boolean isIrRange(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < N;
    }

    // 방향을 바꿔야 하는지 체크 메서드
    private static void checkDir() {
        if (!visited[tornado.x + dx[(tornado.dir + 1) % 4]][tornado.y + dy[(tornado.dir + 1) % 4]]) {
            tornado.dir = (tornado.dir + 1) % 4;
        }
    }
}
