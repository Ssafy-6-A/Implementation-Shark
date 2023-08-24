package kimyongjun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * 풀이 시작 : 6:40
 * 풀이 완료 : 7:30
 * 풀이 시간 : 50분
 *
 * 문제 해석
 * N * N 크기의 교실, N^2명의 학생이 있다.
 * 모든 학생은 1 ~ N^2까지의 번호가 매겨져 있다.
 * 학생의 순서와 각 학생이 좋아하는 4명의 학생을 조사했을 때
 * 아래의 규칙을 따라 정해진 순서대로 학생의 자리를 정해야 한다.
 * 1. 비어 있는 칸 중에서 좋아하는 학생이 인접한 칸에 가장 많은 칸으로 자리를 정한다.
 * 2. 1을 만족하는 칸이 여러 개라면 인접한 칸 중에서 비어 있는 칸이 가장 많은 칸으로 자리를 정한다.
 * 3. 2를 만족하는 칸이 여러 개라면 행의 번호가 가장 작은 순, 열의 번호가 가장 작은 칸으로 자리를 정한다.
 * 자리를 모두 정했으면 만족도를 계산한다.
 * 만족도는 인접한 자리의 좋아하는 학생의 수에 따라 결정된다 (0 = 0, 1 = 1, 2 = 10, 3 = 100, 4 = 1000)
 * 학생 만족도의 총 합을 구해야 한다.
 *
 * 구해야 하는 것
 * 학생 만족도의 총 합
 *
 * 문제 입력
 * 첫째 줄 : 교실의 크기 N
 * 둘째 줄 ~ N^2개 줄 : 학생의 번호, 학생이 좋아하는 4명의 친구
 *
 * 제한 요소
 * 3 <= N <= 20
 *
 * 생각나는 풀이
 * 조건에 따라 구현
 * 입력 순서대로 학생이 앉을 자리를 탐색
 * 2차원 배열을 생성해 학생이 좋아하는 친구 목록을 저장 friend[i][4] = i번 학생이 좋아하는 학생들
 * 모든 칸에 대해 탐색하면서 현재 칸 인접한 칸에서 좋아하는 친구 수 cnt 세고
 * cnt가 커질 때마다 좌표 갱신, cnt가 4라면 이후 탐색할 필요 없으니 break
 * 좌표 저장 클래스는 따로 필요 없을듯, 메서드 멤버 변수로 사용
 * 자리 전부 정한 후 만족도 계산 => 모든 칸 돌면서 사방탐색, cnt 세서 배열에 미리 {0, 1, 10, 100, 1000} 저장해놓고 해당 배열 cnt인덱스 더하면 됨
 *
 * 구현해야 하는 기능
 * 1. 교실 상태 저장할 2차원 배열
 * 2. 친한 친구 목록 저장할 2차원 배열
 * 3. 사방 탐색 델타 배열
 * 4. 최적의 자리를 탐색하는 메서드
 * 5. 점수 합 구하는 메서드
 */
public class BOJ_21608_상어초등학교 {
    static int N;
    static int[][] classRoom; // * 1. 교실 상태 저장할 2차원 배열
    static HashSet<Integer>[] friend; // * 2. 친한 친구 목록 저장할 2차원 배열
    static int[] dx = {-1, 1, 0, 0}; // * 3. 사방 탐색 델타 배열
    static int[] dy = {0, 0, -1, 1}; // * 3. 사방 탐색 델타 배열

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());
        classRoom = new int[N][N];
        friend = new HashSet[N * N + 1]; // 번호가 1부터 시작하므로 패딩

        for (int i = 0; i < N * N; i++) {
            st = new StringTokenizer(br.readLine());
            int number = Integer.parseInt(st.nextToken());
            friend[number] = new HashSet<>(); // number번 학생의 친한 친구를 저장할 HashSet
            for (int j = 0; j < 4; j++) { // 친한 친구 4명 저장
                friend[number].add(Integer.parseInt(st.nextToken()));
            }
            findSeat(number);
        }

        System.out.println(getSum());
    }

    // * 5. 점수 합 구하는 메서드
    private static int getSum() {
        int sum = 0;
        int[] satisfactionPoint = {0, 1, 10, 100, 1000}; // 인접 친구 수에 따른 점수

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int cnt = 0;
                for (int dir = 0; dir < 4; dir++) {
                    int nextX = i + dx[dir];
                    int nextY = j + dy[dir];
                    if (!isInRange(nextX, nextY)) continue;
                    if (friend[classRoom[i][j]].contains(classRoom[nextX][nextY])) cnt++;
                }
                sum += satisfactionPoint[cnt];
            }
        }
        return sum;
    }

    // * 4. 최적의 자리를 탐색하는 메서드
    private static void findSeat(int number) {
        int maxFriendCnt = 0;
        int maxZeroCnt = 0;
        int x = -1, y = -1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (classRoom[i][j] != 0) continue; // 이미 앉은 사람이 있다면 스킵
                if (x == -1) { // 빈 칸 중 가장 위, 왼쪽에 있는 칸을 초기 칸으로 설정
                    x = i;
                    y = j;
                }

                int friendCnt = 0; // 현재 칸에서 인접 친구 수
                int zeroCnt = 0; // 현재 칸에서 인접 빈 자리 수

                for (int dir = 0; dir < 4; dir++) {
                    int nextX = i + dx[dir];
                    int nextY = j + dy[dir];

                    if (!isInRange(nextX, nextY)) continue;
                    if (friend[number].contains(classRoom[nextX][nextY])) {
                        friendCnt++;
                    } else if (classRoom[nextX][nextY] == 0) {
                        zeroCnt++;
                    }
                }

                if (maxFriendCnt < friendCnt) { // 인접한 친구가 가장 많은 칸일 경우
                    maxFriendCnt = friendCnt;
                    maxZeroCnt = zeroCnt;
                    x = i;
                    y = j;
                    continue;
                }

                if (maxFriendCnt == friendCnt && maxZeroCnt < zeroCnt) { // 인접한 친구가 가장 많은 칸과 친구 수가 같고 빈 자리가 많은 경우
                    maxZeroCnt = zeroCnt;
                    x = i;
                    y = j;
                }
            }
        }

        classRoom[x][y] = number; // 최적의 자리에 앉힘
    }

    // 배열 범위내인지 체크하는 메서드
    private static boolean isInRange(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < N;
    }
}
