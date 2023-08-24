package kimyongjun;
/**
 * 풀이 시작 : 7:47
 * 풀이 완료 : 10:03
 * 풀이 시간 : 136분
 * 문제를 잘못 읽어서 삽질함;; 똑바로 읽자
 *
 * 문제 해석
 * N * N인 격자에 파이어볼 M개 발사
 * i번 파이어볼 위치는 (Ri, Ci), 질량은 Mi, 방향은 Di, 속력은 Si
 * 격자의 1번 행과 N번 행은 연결되어 있고, 격자의 1번 열과 N번 열도 연결되어 있다
 * 파이어볼의 방향은 어떤 칸과 인접한 8개의 칸의 방향을 의미, 12시부터 0, 시계방향으로 +1씩 증가
 * ⌊ 숫자 ⌋ = 숫자 이하의 가장 큰 정수
 * 마법사 상어가 파이어볼에 이동 명령하면 아래 과정 수행
 * 1. 모든 파이어볼이 자신의 방향 Di로 속도 Si만큼 이동
 *  - 이동하는 중에는 같은 칸에 여러 개의 파이어볼 존재할 수 있음
 * 2. 이동이 끝난 뒤 2개 이상의 파이어볼이 있는 칸에서는 아래 과정 수행
 *  2-1. 같은 칸에 있는 파이어볼 전부 합쳐짐
 *  2-2. 파이어볼은 4개의 파이어볼로 나뉨
 *  2-3. 나뉜 파이어볼의 질량, 속력, 방향은 다음과 같음
 *      질량 : ⌊ 합쳐진 파이어볼 / 5 ⌋
 *      속력 : ⌊ 합쳐진 파이어볼 속력 합 / 합쳐진 파이어볼 개수 ⌋
 *      방향 : 합쳐지는 파이어볼의 방향이 모두 홀수 or 모두 짝수면 0, 2, 4, 6방향, 그렇지 않다면 1, 3, 5, 7방향
 *      질량이 0인 파이어볼은 소멸
 *
 * 구해야 하는 것
 * 이동을 K번 명령한 후 남아있는 파이어볼 질량의 합
 *
 * 문제 입력
 * 첫째 줄 N = 배열의 가로세로, M = 파이어볼 갯수, K = 이동 명령 횟수
 * 둘째줄 ~ M개 줄 : 파이어볼 정보
 * R = 행 위치, C = 열 위치, M = 질량, S = 속력, D = 방향
 *
 * 제한 요소
 * 4 <= N <= 50
 * 0 <= M <= N^2
 * 1 <= K <= 1000
 * 1 <= r, c <= N
 * 1 <= m <= 1000
 * 1 <= s <= 1000
 * 0 <= d <= 7
 *
 * 생각나는 풀이
 * 시뮬레이션
 * 맨 위 행과 아래 행, 맨 앞 열과 아래 열은 연결되어 있는걸 주의해야 함
 * 매 칸마다 파이어볼의 정보를 저장해야 할 것 같다 => 어떤 자료구조? ArrayList?
 * 이동하는 파이어볼은 어디에? 모든 칸 돌면서 파이어볼을 Queue에 넣고 Queue에서 빼면서 ArrayList에 다시 넣기? 아니면 파이어볼에 몇턴인지 변수 넣고 모든 칸을 Queue로?
 * 배열 칸 넘는 이동 처리 = 1000넘는 N배수 구한 후 (N배수 + s) % N이 최종 위치일듯
 * 합쳐지는 파이어볼 방향 판별? => 합쳐질 때마다 & 1 연산해서 sum 더해줌 => sum % 합치는 개수 = 0이면 0246, 아니면 1357
 *
 * 구현해야 하는 기능
 * 1. N * N의 Queue 배열
 * 2. 파이어볼 클래스
 *  멤버 : r c m s d turn
 *  메서드 : move()
 * 3. move() 구현
 * 4. 모든 칸 탐색하며 Queue size가 2개 이상인 칸에 대해서는 fusion() 수행
 * 5. K번 반복 (파이어볼 없으면 반복 종료 후 0 리턴)
 *
 *
 * ----- 설계 잘못함 -----
 * 처음 파이어볼은 맵에 있는 게 아니고 이동 대기 상태 => 이동 대기 시킬 Fireball 리스트 필요
 * 프로그램 수행 순서
 * 1. 이동 대기 리스트에 있는 파이어볼을 하나씩 목적지로 이동시킴
 * 2. 각 파이어볼의 목적지 칸에 저장
 * 3. 모든 파이어볼의 이동이 끝났다면 map 배열 탐색하며 맵 칸 비움
 * 3-1. map 칸의 파이어볼 개수가 2개 이상이면 병합 후 맵 칸 비움
 * 4. 병합 이전 파이어볼을 이동 대기 리스트에서 제거
 * 5. 파이어볼을 조건에 맞게 4개로 분열시켜 이동 대기 리스트에 추가
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class BOJ_20056_마법사상어와파이어볼 {
    static int N;
    static Queue<FireBall>[][] map; // 현재 칸에 있는 파이어볼 저장하기 위해 Queue 2차원 배열
    static List<FireBall> fireBalls = new ArrayList<>(); // 이동해야 할 파이어볼이 들어있는 리스트
    static int[] dx = {-1, -1, 0, 1, 1, 1, 0, -1}; // 8방향
    static int[] dy = {0, 1, 1, 1, 0, -1, -1, -1}; // 8방향
    static int[][] directions = {{0, 2, 4, 6}, {1, 3, 5, 7}}; // 병합 후 분열할 때 사용할 방향 배열

    static class FireBall {
        int x, y, mass, speed, dir; // 행좌표, 열좌표, 질량, 속력, 방향

        public FireBall(int x, int y, int mass, int speed, int dir) {
            this.x = x;
            this.y = y;
            this.mass = mass;
            this.speed = speed;
            this.dir = dir;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());

        map = new Queue[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                map[i][j] = new ArrayDeque<>();
            }
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1; // 입력으로 들어오는 행, 열 좌표 범위가 1 <= x, y <= N이므로 제로인덱스와 맞춰줌
            int y = Integer.parseInt(st.nextToken()) - 1; // 입력으로 들어오는 행, 열 좌표 범위가 1 <= x, y <= N이므로 제로인덱스와 맞춰줌
            int m = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

            fireBalls.add(new FireBall(x, y, m, s, d));
        }

        for (int i = 0; i < K; i++) {
            move(); // * 1. 이동 대기 리스트에 있는 파이어볼을 하나씩 목적지로 이동
            checkFireballFusion(); // * 3. 모든 파이어볼의 이동이 끝났다면 map 배열 탐색하며 맵 칸 비움
        }

        System.out.println(getMass());
    }

    // 이동이 끝나고 남은 총 질량 구하는 메서드
    private static int getMass() {
        int sum = 0;
        for (FireBall f : fireBalls) sum += f.mass;
        return sum;
    }

    // * 이동 대기 리스트에 있는 파이어볼을 하나씩 목적지로 이동시키는 메서드
    private static void move() {
        // 배열의 칸 벗어나면 반대쪽 끝으로 돌아가야 함.
        // 델타배열을 속도만큼 반복하는 방법도 있지만, 속도가 최대 1000이므로 매 파이어볼의 움직임마다 반복하는 건 비효율적
        // 식을 만들어 한 번의 계산으로 목적지를 도출
        // 파이어볼의 목적지 좌표 = (N + 파이어볼의 현재 좌표 + (방향 * 속도) % N) % N
        // 맨 앞 N을 더하는 이유는 방향이 음수일 때에도 결과값이 항상 양수가 나올 수 있도록 처리한 것
        // (방향 * 속도) % N은 계산 과정에서 항상 0 ~ N - 1 사이의 값이 나오도록 하는 모듈러 연산, 마지막 % N도 동일
        for (FireBall f : fireBalls) {
            f.x = (N + f.x + (dx[f.dir] * f.speed) % N) % N;
            f.y = (N + f.y + (dy[f.dir] * f.speed) % N) % N;
            map[f.x][f.y].offer(f); // * 2. 각 파이어볼의 목적지 칸에 저장
        }
    }

    // * map 배열 탐색하며 맵 칸 비우는 메서드
    private static void checkFireballFusion() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j].size() >= 2) { // * 3-1. map 칸의 파이어볼 개수가 2개 이상이면 병합 후 맵 칸 비움
                    fusionNDivide(i, j);
                } else map[i][j].clear(); // 1개일 때에도 어차피 이동 리스트에 파이어볼 정보가 들어 있으므로 중복 계산 안되게 지움
            }
        }
    }

    // 현재 칸의 파이어볼을 병합 후 분열시키는 메서드
    private static void fusionNDivide(int x, int y) {
        int nextMass = 0;
        int nextSpeed = 0;
        int dir = 0;
        int cnt = map[x][y].size(); // Queue의 사이즈 = 현재 칸의 파이어볼 갯수

        // 병합 과정
        while (!map[x][y].isEmpty()) {
            FireBall now = map[x][y].poll();
            nextMass += now.mass;
            nextSpeed += now.speed;
            dir += (now.dir & 1); // dir += (now.dir % 2 == 0) ? 0 : 1과 동일한 연산
            fireBalls.remove(now); // * 4. 병합 이전 파이어볼을 이동 대기 리스트에서 제거
        }

        // * 5. 파이어볼을 조건에 맞게 4개로 분열시켜 이동 대기 리스트에 추가
        // 분열 과정
        if (nextMass < 5) return; // 병합된 총 질량이 5 미만이면 5로 나눴을 때 0이 되므로 분열하면 사라진다
        nextMass /= 5;
        nextSpeed /= cnt;
        dir = (dir % cnt == 0) ? 0 : 1; // (dir % cnt == 0)이 되는 경우는 dir == 0 OR dir == cnt인 경우, 즉 모두 짝수거나 모두 홀수일 때밖에 없다

        for (int i = 0; i < 4; i++) { // directions[][] = {{0, 2, 4, 6}, {1, 3, 5, 7}}이니까 dir이 0이면 0246방향, 1이면 1357방향
            fireBalls.add(new FireBall(x, y, nextMass, nextSpeed, directions[dir][i]));
        }
    }
}
