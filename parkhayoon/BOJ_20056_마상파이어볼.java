package algo.week4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
문제
마법사 상어가 크기가 N×N인 격자에 파이어볼 M개를 발사
가장 처음에 파이어볼은 각자 위치에서 이동을 대기
i번 파이어볼의 위치는 (ri, ci)(r행 c열), 질량은 mi이고, 방향은 di, 속력은 si,
격자의 행과 열은 1번부터 N번까지 번호, 1번 행은 N번과 연결되어 있고, 1번 열은 N번 열과 연결
파이어볼의 방향은 어떤 칸과 인접한 8개의 칸의 방향을 의미하며, 정수로는 다음과 같음
7	0	1
6	 	2
5	4	3
마법사 상어가 모든 파이어볼에게 이동을 명령하면 다음이 일들이 일어난다.
1.	모든 파이어볼이 자신의 방향 di로 속력 si칸 만큼 이동
 	이동하는 중에는 같은 칸에 여러 개의 파이어볼이 있을 수도 있음
2.	이동이 모두 끝난 뒤, 2개 이상의 파이어볼이 있는 칸에서는 다음과 같은 일이 일어남
1.	같은 칸에 있는 파이어볼은 모두 하나로 합쳐짐
2.	파이어볼은 4개의 파이어볼로 나누어짐
3.	나누어진 파이어볼의 질량, 속력, 방향
1.	질량은 ⌊(합쳐진 파이어볼 질량의 합)/5⌋
2.	속력은 ⌊(합쳐진 파이어볼 속력의 합)/(합쳐진 파이어볼의 개수)⌋
3.	합쳐지는 파이어볼의 방향이 모두 홀수이거나 모두 짝수이면, 방향은 0, 2, 4, 6, 그렇지 않으면 1, 3, 5, 7
4.	질량이 0인 파이어볼은 소멸
마법사 상어가 이동을 K번 명령한 후, 남아있는 파이어볼 질량의 합

입력
첫째 줄: N, M, K
M개의 줄: 파이어볼의 정보가 한 줄에 하나씩, 파이어볼의 정보는 다섯 정수 ri, ci, mi, si, di
서로 다른 두 파이어볼의 위치가 같은 경우는 입력으로 주어지지 않음

출력
마법사 상어가 이동을 K번 명령한 후, 남아있는 파이어볼 질량의 합

제한
•	4 ≤ N ≤ 50
•	0 ≤ M ≤ N2
•	1 ≤ K ≤ 1,000
•	1 ≤ ri, ci ≤ N
•	1 ≤ mi ≤ 1,000
•	1 ≤ si ≤ 1,000
•	0 ≤ di ≤ 7

Idea
파이어볼(위치 x, 위치 y, 질량, 속력, 방향)
1. 모든 파이어볼이 자신의 방향 di로 속력 si칸 만큼 이동
2. 이동 후, 2개 이상 파이어볼이 한 칸에 있는 경우
1. 하나의 파이어볼로 합쳐진 뒤 4개로 나뉨
    질량: ⌊(합쳐진 파이어볼 질량의 합)/5⌋
속력: ⌊(합쳐진 파이어볼 속력의 합)/(합쳐진 파이어볼의 개수)⌋
합쳐지는 파이어볼의 방향이 모두 홀수 or 모두 짝수이면, 방향은 0, 2, 4, 6, 그렇지 않으면 1, 3, 5, 7
    2. 만약 질량이 0이라면 소멸
상어가 이동을 K번 명령한 후, 남아있는 파이어볼 질량의 합

4 2 1
1 1 5 2 2
1 4 7 1 6

(1,1), m=5, s=2, d=2
(1,4), m=7, s=1, d=6

k=1
(1+0,1+2) = (1,3)
(1+0,4-1) = (1,3)
m=(5+7)/5 = 2
s=(2+1)/2 = 1
(1,3), m=2, s=1, d=0,2,4,6
 */
class Fireball { // 파이어볼의 정보
    int row; // 파이어볼의 위치(행)
    int col; // 파이어볼의 위치(열)
    int mass; // 파이어볼의 질량
    int speed; // 파이어볼의 속력
    int direction; // 파이어볼의 방향

    Fireball(int row, int col, int mass, int speed, int direction) {
        this.row = row;
        this.col = col;
        this.mass = mass;
        this.speed = speed;
        this.direction = direction;
    }
}

public class BOJ_20056_마상파이어볼 {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken()); // 정사각형의 한 변의 크기
        int M = Integer.parseInt(st.nextToken()); // 초기 파이어볼의 개수
        int K = Integer.parseInt(st.nextToken()); // 마법사 상어가 명령한 이동 횟수
        ArrayList<Fireball> fireballList = new ArrayList<Fireball>();

        for(int i=0; i<M; i++) {
            st = new StringTokenizer(br.readLine()); // 각 파이어볼의 정보
            int r = Integer.parseInt(st.nextToken()); // 파이어볼의 행
            int c = Integer.parseInt(st.nextToken()); // 파이어볼의 열
            int m = Integer.parseInt(st.nextToken()); // 파이어볼의 질량
            int s = Integer.parseInt(st.nextToken()); // 파이어볼의 속력
            int d = Integer.parseInt(st.nextToken()); // 파이어볼의 방향
            Fireball fb = new Fireball(r, c, m, s, d);
            fireballList.add(fb); // 파이어볼 목록에 추가
        }

        // 파이어볼 방향 정보에 따른 이동 방법
        int dir[][] = {{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
        for(int loop=0; loop<K; loop++) { // K번 이동 횟수 실시
            int[][] fireballCount = new int[N+1][N+1]; // 파이어볼이 한 칸에 몇 개인지 확인
            //System.out.println(loop);
            //////////////////////////////////////////////////
            // 1. 모든 파이어볼이 방향 d로 속력 s만큼 이동
            // 1번행은 N번행과 연결, N번행->1번행->2번행...(=N의 나머지, 0행인 경우는 N번행)
            for(int i=0; i<fireballList.size(); i++) {
                fireballList.get(i).row += dir[fireballList.get(i).direction][0]*fireballList.get(i).speed; // 행 위치 갱신
                //while(fireballList.get(i).row>N) fireballList.get(i).row-=N; // N을 넘어가는 경우
                if(fireballList.get(i).row>N) fireballList.get(i).row%=N;
                //while(fireballList.get(i).row<=0) fireballList.get(i).row+=N; // 1을 넘어가는 경우
                if(fireballList.get(i).row<=0) {
                    fireballList.get(i).row%=N;
                    fireballList.get(i).row+=N;
                }
                fireballList.get(i).col += dir[fireballList.get(i).direction][1]*fireballList.get(i).speed; // 열 위치 갱신
                //while(fireballList.get(i).col>N) fireballList.get(i).col-=N; // N을 넘어가는 경우
                if(fireballList.get(i).col>N) fireballList.get(i).col%=N;
                //while(fireballList.get(i).col<=0) fireballList.get(i).col+=N; // 1을 넘어가는 경우
                if(fireballList.get(i).col<=0) {
                    fireballList.get(i).col%=N;
                    fireballList.get(i).col+=N;
                }
                fireballCount[fireballList.get(i).row][fireballList.get(i).col]++;
            }
            //for(int i=0; i<fireballList.size(); i++)
            //System.out.println(fireballList.get(i).row+" "+fireballList.get(i).col+" "+fireballList.get(i).mass+" "+fireballList.get(i).speed+" "+fireballList.get(i).direction);
            // 2. 이동이 모두 끝난 뒤, 2개 이상의 파이어볼이 같은 칸에 있는지 확인

            // row, col, fireball 순으로 탐색 - 시간초과
            // 특정 row, col에 몇 개의 fireball이 있는지 확인
            for(int i=1; i<=N; i++) { // row 확인
                for(int j=1; j<=N; j++) { // col 확인
                    if(fireballCount[i][j]<2) continue; // 현재 칸에 파이어볼이 2개 미만이라면 확인하지 않고 통과
                    int fbCount = 0; // 같은 칸의 파이어볼 수
                    int newMass = 0; // 합쳐질 파이어볼의 질량
                    int newSpeed = 0; // 합쳐질 파이어볼의 속력
                    ArrayList<Integer> dirList = new ArrayList<Integer>(); // 같은 칸의 파이어볼의 방향 목록
                    ArrayList<Fireball> curFbPos = new ArrayList<Fireball>(); // 같은 칸의 파이어볼 목록
                    for(int f=0; f<fireballList.size(); f++) {
                        //System.out.println(i+" "+j);
                        //if(i==7&&j==5)
                        //System.out.println(fireballList.get(f).mass+" "+fireballList.get(f).speed+" "+fireballList.get(f).direction);
                        if(i==fireballList.get(f).row && j==fireballList.get(f).col) { // 같은 칸의 파이어볼인 경우
                            //System.out.println(fireballList.get(f).mass);
                            fbCount++;
                            newMass+=fireballList.get(f).mass;
                            newSpeed+=fireballList.get(f).speed;
                            dirList.add(fireballList.get(f).direction);
                            curFbPos.add(fireballList.get(f));
                        }
                    }
                    // System.out.println(i+" "+j+" "+newMass);
                    if(fbCount>=2) { // 같은 칸의 파이어볼이 2개 이상인 경우
                        newMass/=5;
                        // 파이어볼의 합이 0인 경우 소멸(기존 합해진 파이어볼 제거)
                        //System.out.println(i+" "+j+" "+newMass);
                        if(newMass==0) {
                            for(Fireball fb : curFbPos)
                                fireballList.remove(fb);
                            continue;
                        }
                        newSpeed/=fbCount;
                        int checkDir = dirList.get(0)%2;
                        int[] isDir = {0,2,4,6};
                        int[] isNotDir = {1,3,5,7};
                        for(int newDir: dirList) {
                            if(newDir%2!=checkDir) {
                                isDir = isNotDir;
                                break;
                            }
                        }
                        // 기존 합해진 파이어볼 제거
                        for(Fireball fb : curFbPos)
                            fireballList.remove(fb);
                        // 신규 파이어볼 추가
                        for(int k=0; k<4; k++)
                            fireballList.add(new Fireball(i,j,newMass,newSpeed,isDir[k]));
                    }
                }
            }

            //for(int i=0; i<fireballList.size(); i++)
            //System.out.println(fireballList.get(i).row+" "+fireballList.get(i).col+" "+fireballList.get(i).mass+" "+fireballList.get(i).speed+" "+fireballList.get(i).direction);
            //////////////////////////////////////////////////
        }
        // 남아있는 질량의 합 출력
        int massSum = 0;
        for(Fireball fb : fireballList)
            massSum += fb.mass;
        System.out.println(massSum);
    }

}
