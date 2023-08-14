package algostudy.week4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
교실 크기: NxN, (1,1)~(N,N), 3 ≤ N ≤ 20
학생 수: N^2명, 1~N^2까지 번호, 번호 중복 X
학생의 순서, 각 학생이 좋아하는 학생 4명
한 칸에는 학생 한 명만
|r1 - r2| + |c1 - c2| = 1을 만족하는 두 칸이 (r1, c1)과 (r2, c2)를 인접하다고 함
=> 상, 하, 좌, 우
1. 비어있는 칸 중에서 좋아하는 학생이 인접한 칸에 가장 많은 칸으로 자리를 정한다.
2. 1을 만족하는 칸이 여러 개이면, 인접한 칸 중에서 비어있는 칸이 가장 많은 칸으로 자리를 정한다.
3. 2를 만족하는 칸도 여러 개인 경우에는 행의 번호가 가장 작은 칸으로,
그러한 칸도 여러 개이면 열의 번호가 가장 작은 칸으로 자리를 정한다.
학생의 만족도: 그 학생과 인접한 칸에 앉은 좋아하는 학생의 수
학생수 0: 만족도 0, 1: 1, 2: 10, 3: 100, 4: 1000
학생의 만족도의 총 합

입력
첫째 줄 N
N^2 줄에 학생의 번호, 그 학생이 좋아하는 학생 4명의 번호(모두 다른 학생, 자기 자신 X)

 */
public class BOJ_21608_상어초 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int stuArr[][] = new int[n][n]; // 학생 배치 정보

        int stuOrder[] = new int[n*n]; // 학생 자리 선택 순서
        int prefArr[][] = new int[n*n+1][4]; // 각 학생의 선호 학생 목록
        for(int i=0; i<n*n; i++) {
            st = new StringTokenizer(br.readLine());
            int stu = Integer.parseInt(st.nextToken());
            stuOrder[i] = stu;
            for(int j=0; j<4; j++)
                prefArr[stu][j] = Integer.parseInt(st.nextToken());
        }

        //System.out.println(Arrays.toString(stuOrder));
        //System.out.println(Arrays.deepToString(prefArr));

        // 인접 학생 위치
        int[][] deltas = {{0,-1},{0,1},{-1,0},{1,0}};

        for(int stu : stuOrder) {
            // 1. 비어있는 칸 중에서 좋아하는 학생이 인접한 칸에 가장 많은 칸으로 자리를 정한다.
            // 2. 1을 만족하는 칸이 여러 개이면, 인접한 칸 중에서 비어있는 칸이 가장 많은 칸으로 자리를 정한다.
            // 3. 2를 만족하는 칸도 여러 개인 경우에는 행의 번호가 가장 작은 칸으로,
            // 그러한 칸도 여러 개이면 열의 번호가 가장 작은 칸으로 자리를 정한다.

            int x = 0; // 학생에게 최적인 위치 저장
            int y = 0;
            int maxFriends = 0; // 최대 근접 친구 수 저장
            int maxSpace = 0; // 최대 근접 빈 공간 수 저장
            boolean isChanged = false; // 학생이 최적의 자리를 정했는지 확인
            for(int i=0; i<n; i++) { // 학생 배치도 탐색
                for(int j=0; j<n; j++) {
                    if(stuArr[i][j]!=0) continue; // 학생이 배치되지 않은 장소만 탐색

                    int friends = 0; // 현재 위치 주변의 친구 수
                    int space = 0; // 현재 위치 주변의 빈 공간 수
                    for(int d=0; d<4; d++) { // 상하좌우 탐색
                        int dx = i+deltas[d][0];
                        int dy = j+deltas[d][1];
                        if(dx>=0 && dx<n && dy>=0 && dy<n) { // 자리 배치 범위 내에서
                            for(int f=0; f<4; f++) { // 주변 학생이 선호 학생인지 확인
                                // System.out.println(stuArr[x][y]+" "+f+" "+prefArr[stu][f]);
                                if(stuArr[dx][dy]==prefArr[stu][f])
                                    friends++;
                            }
                            if(stuArr[dx][dy]==0) space++; // 주변 공간이 빈 공간인지 확인
                        }
                    }
                    // 1. 비어있는 칸 중에서 좋아하는 학생이 인접한 칸에 가장 많은 칸으로 자리를 정한다.
                    //System.out.println(friends+" "+maxFriends+" "+space+" "+maxSpace);
                    if(friends>maxFriends) {
                        isChanged = true;
                        maxFriends = friends;
                        maxSpace = space;
                        x = i;
                        y = j;
                    }
                    // 2. 1을 만족하는 칸이 여러 개이면, 인접한 칸 중에서 비어있는 칸이 가장 많은 칸으로 자리를 정한다.
                    else if(friends==maxFriends) {
                        if(space>maxSpace) {
                            isChanged = true;
                            maxSpace = space;
                            x = i;
                            y = j;
                        }
                        // 3. 2를 만족하는 칸도 여러 개인 경우에는 행의 번호가 가장 작은 칸으로,
                        else if(space==maxSpace) {
                            if(i<x) {
                                isChanged = true;
                                x = i;
                                y = j;
                            }
                            // 그러한 칸도 여러 개이면 열의 번호가 가장 작은 칸으로 자리를 정한다.
                            else if(i==x) {
                                if(j<y) {
                                    isChanged = true;
                                    y = j;
                                }
                            }
                        }
                    }
                }
            }
            if(isChanged) // 만약 갈 자리를 정했다면 그 자리로 이동
                stuArr[x][y] = stu;
            else // 만약 갈 자리를 정하지 못한 경우, 남은 자리 중 행의 번호가 가장 작은 칸, 그 다음은 열이 가장 작은 칸으로 이동
                searchzero: for(int i=0; i<n; i++) {
                    for(int j=0; j<n; j++) {
                        if(stuArr[i][j]==0) {
                            stuArr[i][j] = stu;
                            break searchzero;
                        }
                    }
                }
            //System.out.println(Arrays.deepToString(stuArr));
        }



        int result = 0;
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                int finalFriends = 0;
                for(int d=0; d<4; d++) {
                    int dx = i+deltas[d][0];
                    int dy = j+deltas[d][1];
                    if(dx>=0 && dx<n && dy>=0 && dy<n) {
                        for(int f=0; f<4; f++) {
                            if(stuArr[dx][dy]==prefArr[stuArr[i][j]][f])
                                finalFriends++;
                        }
                    }
                }
                // System.out.println(finalFriends);
                if(finalFriends==0) continue;
                result += (int)Math.pow(10, finalFriends-1);
            }
        }
        System.out.println(result);
    }

}
