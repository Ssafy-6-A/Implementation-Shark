package algo.week4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
백준 20057 마상토네이도
NxN 크기의 모래밭, 위치(r,c) = r행 c열, A[r][c] = (r,c)에 있는 모래의 양
토네이도 시전 시 격자 가운데 칸부터 토네이도 이동 시작, 한 번에 한 칸 이동

토네이도가 한 칸 이동할 때마다 모래는 다음과 같이 일정한 비율로 흩날리게 된다.

토네이도가 x에서 y로 이동하면 y의 모든 모래가 비율과 a가 적혀있는 칸으로 이동
비율에 적혀있는 칸으로 이동하는 모래의 양 = y에 있는 모래의 해당 비율만큼(소수점 아래 버림)
a로 이동하는 모래의 양 = 비율이 적혀있는 칸으로 이동하지 않은 남은 모래의 양
모래가 이미 있는 칸으로 모래가 이동하면 모래가 더해짐
위 그림은 토네이도 왼쪽 이동 시 그림, 다른 방향 이동 시 해당 방향으로 회전
토네이도는 (1,1)까지 이동 후 소멸, 모래는 격자 밖으로 이동 가능
격자 밖으로 나간 모래의 양은?

입력
첫째 줄: 격자의 크기 N(3 ≤ N ≤ 499, N은 홀수)
N개의 줄: 격자의 각 칸에 있는 모래, r번째 줄에서 c번째 주어지는 정수는 A[r][c]
0 ≤ A[r][c] ≤ 1,000, 가운데 칸 모래의 양은 0

출력
격자의 밖으로 나간 모래의 양 출력

명세서
중앙으로 이동(N/2+1, N/2+1)
이동 방향
N=3: 좌(1)-하(1)-우(2)-상(2)-좌(2)
N=5: 2*(좌(1,3)-하(1,3)-우(2,4)-상(2,4))-좌(4)
N=7: 3*(좌(1,3,5)-하(1,3,5)-우(2,4,6)-상(2,4,6))-좌(6)
=> 1,1(좌,하),2,2(우,상),3,3,4,4,5,5,6,6, ...,
이동할 때 마다 모래 이동, 이동횟수: N^2-1
If 범위 밖: 밖으로 간 모래+=이동한 모래
Else: 배열 내에 추가

 */
public class BOJ_20057_마상토네이도 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int arr[][] = new int[n+1][n+1];

        for(int i=1; i<=n; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=1; j<=n; j++)
                arr[i][j] = Integer.parseInt(st.nextToken());
        }

        // 토네이도에 의한 모래 이동(좌측 기준)
        /*
        arr[x][y]
        arr[x+1][y-1] = arr[x][y]*0.10
        arr[x-1][y-1] = arr[x][y]*0.10
        arr[x+1][y] = arr[x][y]*0.07
        arr[x-1][y] = arr[x][y]*0.07
        arr[x][y-2] = arr[x][y]*0.05
        arr[x+2][y] = arr[x][y]*0.02
        arr[x-2][y] = arr[x][y]*0.02
        arr[x-1][y+1] = arr[x][y]*0.01
        arr[x+1][y+1] = arr[x][y]*0.01
        arr[x][y-1] = arr[x][y]의 나머지
         */
        int delta[][] = {{0,-1},{1,0},{0,1},{-1,0}}; // 0: 좌, 1: 하, 2: 우, 3: 상
        int x = n/2+1; // 토네이도 현재 위치
        int y = n/2+1;
        int rotation = 0; // 회전 방향
        int outSand = 0;
        int rotationCount = 1;
        Loop: while(x!=1 || y!=1) {
            // 진행 방향으로 이동
            // 진행 방향으로 모래 뿌리기
            if(rotation%4<2) {
                // 좌측 진행
                for(int r=0; r<rotationCount; r++) {
                    x += delta[rotation%4][0];
                    y += delta[rotation%4][1];
                    //System.out.println(x+" "+y);
                    if (rotation % 4 == 0) {
                        int currentSand = arr[x][y];
                        //if (y-1>0) arr[x][y-1] = (int) (arr[x][y] * 0.55);
                        //else outSand += (int) (arr[x][y] * 0.55);
                        if (x+1<=n && y-1>0) arr[x+1][y-1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x-1>0 && y-1>0) arr[x-1][y-1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x+1<=n) arr[x+1][y] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (x-1>0) arr[x-1][y] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (y-2>0) arr[x][y-2] += (int) (currentSand * 0.05);
                        else outSand += (int) (currentSand * 0.05);
                        arr[x][y] -= (int) (currentSand * 0.05);
                        if (x+2<=n) arr[x+2][y] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x-2>0) arr[x-2][y] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x-1>0 && y+1<= n) arr[x-1][y+1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        if (x+1<=n && y+1 <= n) arr[x+1][y+1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        int newInt = arr[x][y];
                        if (y-1>0) arr[x][y-1] += newInt;
                        else outSand += newInt;
                    }
                    arr[x][y] = 0;
                    //System.out.println(Arrays.deepToString(arr));
                    if(x==1 && y==1) break Loop;
                }
                rotation++;
                for(int r=0; r<rotationCount; r++) {
                    x += delta[rotation%4][0];
                    y += delta[rotation%4][1];
                    //System.out.println(x+" "+y);
                    // 하측 진행
                    if(rotation%4==1) {
                        int currentSand = arr[x][y];
                        //if (x+1<=n) arr[x+1][y] = (int) (arr[x][y] * 0.55);
                        //else outSand += (int) (arr[x][y] * 0.55);
                        if (x+1<=n && y+1<=n) arr[x+1][y+1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x+1<=n && y-1>0) arr[x+1][y-1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (y+1<=n) arr[x][y+1] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (y-1>0) arr[x][y-1] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (x+2<=n) arr[x+2][y] += (int) (currentSand * 0.05);
                        else outSand += (int) (currentSand * 0.05);
                        arr[x][y] -= (int) (currentSand * 0.05);
                        if (y+2<=n) arr[x][y+2] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (y-2>0) arr[x][y-2] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x-1>0 && y-1>0) arr[x-1][y-1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        if (x-1>0 && y+1<=n) arr[x-1][y+1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        int newInt = arr[x][y];
                        if (x+1<=n) arr[x+1][y] += newInt;
                        else outSand += newInt;
                    }
                    arr[x][y] = 0;
                    //System.out.println(Arrays.deepToString(arr));
                }
                rotation++;
                rotationCount++;
            }
            else {
                // 우측 진행
                for(int r=0; r<rotationCount; r++) {
                    x += delta[rotation%4][0];
                    y += delta[rotation%4][1];
                    //System.out.println(x+" "+y);
                    if (rotation % 4 == 2) {
                        int currentSand = arr[x][y];
                        //if (y+1<=n) arr[x][y+1] = (int) (arr[x][y] * 0.55);
                        //else outSand += (int) (arr[x][y] * 0.55);
                        if (x+1<=n && y+1<=n) arr[x+1][y+1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x-1>0 && y+1<=n) arr[x-1][y+1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x+1<=n) arr[x+1][y] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (x-1>0) arr[x-1][y] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (y+2<=n) arr[x][y+2] += (int) (currentSand * 0.05);
                        else outSand += (int) (currentSand * 0.05);
                        arr[x][y] -= (int) (currentSand * 0.05);
                        if (x+2<=n) arr[x+2][y] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x-2>0) arr[x-2][y] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x-1>0 && y-1>0) arr[x-1][y-1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        if (x+1<=n && y-1>0) arr[x+1][y-1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        int newInt = arr[x][y];
                        if (y+1<=n) arr[x][y+1] += newInt;
                        else outSand+=newInt;
                    }
                    arr[x][y] = 0;
                    //System.out.println(Arrays.deepToString(arr));
                }
                rotation++;
                // 상측 진행
                for(int r=0; r<rotationCount; r++) {
                    x += delta[rotation%4][0];
                    y += delta[rotation%4][1];
                    //System.out.println(x+" "+y);
                    if(rotation%4==3) {
                        int currentSand = arr[x][y];
                        //if (x-1>0) arr[x-1][y] = (int) (currentSand * 0.55);
                        //else outSand += (int) (currentSand * 0.55);
                        if (x-1>0 && y+1<=n) arr[x-1][y+1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (x-1>0 && y-1>0) arr[x-1][y-1] += (int) (currentSand * 0.10);
                        else outSand += (int) (currentSand * 0.10);
                        arr[x][y] -= (int) (currentSand * 0.10);
                        if (y+1<=n) arr[x][y+1] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (y-1>0) arr[x][y-1] += (int) (currentSand * 0.07);
                        else outSand += (int) (currentSand * 0.07);
                        arr[x][y] -= (int) (currentSand * 0.07);
                        if (x-2>0) arr[x-2][y] += (int) (currentSand * 0.05);
                        else outSand += (int) (currentSand * 0.05);
                        arr[x][y] -= (int) (currentSand * 0.05);
                        if (y+2<=n) arr[x][y+2] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (y-2>0) arr[x][y-2] += (int) (currentSand * 0.02);
                        else outSand += (int) (currentSand * 0.02);
                        arr[x][y] -= (int) (currentSand * 0.02);
                        if (x+1<=n && y-1>0) arr[x+1][y-1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        if (x+1<=n && y+1<=n) arr[x+1][y+1] += (int) (currentSand * 0.01);
                        else outSand += (int) (currentSand * 0.01);
                        arr[x][y] -= (int) (currentSand * 0.01);
                        int newInt = arr[x][y];
                        if (x-1>0) arr[x-1][y] += newInt;
                        else outSand+=newInt;
                    }
                    arr[x][y] = 0;
                    //System.out.println(Arrays.deepToString(arr));
                }
                rotation++;
                rotationCount++;
            }
        }
        System.out.println(outSand);
    }

}
