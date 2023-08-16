package algostudy.week4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/*
크기 2^N x 2^N의 얼음판
위치(r,c) A[r][c] = 얼음의 양, 0: 얼음이 없는 경우
파이어스톰을 시전할 때마다 단계 L 결정
격자를 2^L x 2^L 크기의 부분격자로 나눔
모든 부분 격자를 시계방향으로 90도 회전
인접칸에 얼음이 있는 칸 < 3 이면 얼음의 양 1 감소
(r, c)와 인접한 칸: (r-1, c), (r+1, c), (r, c-1), (r, c+1)
파이어스톰을 총 Q번 시전
모든 파이어스톰을 시전한 후
1. 남아있는 얼음 A[r][c]의 합
2. 남아있는 얼음 중 가장 큰 덩어리가 차지하는 칸의 개수
얼음이 있는 칸이 얼음이 있는 칸과 인접해 있으면, 두 칸이 연결되어 있음
덩어리는 연결된 칸의 집합

입력
첫째 줄: N(2 ≤ N ≤ 6), Q(1 ≤ Q ≤ 1,000)
2^N개의 줄: 격자의 각 칸에 있는 얼음의 양
마지막 줄: 마법사 상어가 시전한 단계 L1, L2, ..., LQ가 순서대로

출력
첫째 줄: 남아있는 얼음 A[r][c]의 합을 출력
둘째 줄: 가장 큰 덩어리가 차지하는 칸의 개수를 출력
단, 덩어리가 없으면 0을 출력한다.
 */
public class BOJ_20058_마상파이어스톰 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int q = Integer.parseInt(st.nextToken());

        int boxSize = 1<<n;
        int[][] arr = new int[boxSize][boxSize];
        for(int i=0; i<boxSize; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<boxSize; j++)
                arr[i][j] = Integer.parseInt(st.nextToken());
        }

        int lList[] = new int[q];
        st = new StringTokenizer(br.readLine());
        for(int i=0; i<q; i++)
            lList[i] = Integer.parseInt(st.nextToken());
        int partBoxSize = 0;

        // 파이어스톰을 총 Q번 시전
        for(int loop=0; loop<q; loop++) {
            // 1. 격자를 2^L × 2^L 크기의 부분 격자로 나누기
            partBoxSize = 1 << lList[loop];
            // 2. 모든 부분 격자를 시계 방향으로 90도 회전
            int boxNum = boxSize/partBoxSize;
            int[][] tempBox = new int[partBoxSize][partBoxSize];
            // 모든 부분 격자 탐색
            for(int a=0; a<boxNum; a++) {
                for(int b=0; b<boxNum; b++) {
                    // 현재 부분 격자
                    // 오른쪽으로 90도 회전: (i,j) -> (j,(N-1)-i)
                    for (int j = 0; j < partBoxSize; j++) {
                        for (int k = 0; k < partBoxSize; k++)
                            tempBox[k][(partBoxSize - 1) - j] = arr[a * partBoxSize + j][b * partBoxSize + k];
                    }
                    for (int j = 0; j < partBoxSize; j++) {
                        for (int k = 0; k < partBoxSize; k++)
                            arr[a * partBoxSize + j][b * partBoxSize + k] = tempBox[j][k];
                    }
                }
            }
            //System.out.println(Arrays.deepToString(arr));
            // 3. 음이 있는 칸 3개 또는 그 이상과 인접해있지 않은 칸은 얼음의 양이 1 줄어듦
            int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}};
            int[][] tempMovedBox = new int[boxSize][boxSize];
            for(int i=0; i<boxSize; i++) {
                for(int j=0; j<boxSize; j++) {
                    int iceCount=0;
                    for(int d=0; d<deltas.length; d++) {
                        int dx = i+deltas[d][0];
                        int dy = j+deltas[d][1];
                        if(dx>=0&&dx<boxSize&&dy>=0&&dy<boxSize&&arr[dx][dy]>0)
                            iceCount++;
                    }
                    if(iceCount<3&&arr[i][j]>0)
                        tempMovedBox[i][j] = arr[i][j]-1;
                    else
                        tempMovedBox[i][j] = arr[i][j];
                }
            }
            for(int i=0; i<boxSize; i++) {
                for (int j = 0; j < boxSize; j++)
                    arr[i][j] = tempMovedBox[i][j];
            }
        }
        //System.out.println(Arrays.deepToString(arr));
        // 결과값 도출
        // 1. 남아있는 얼음 A[r][c]의 합
        int sum = 0;
        for(int i=0; i<boxSize; i++) {
            for (int j = 0; j < boxSize; j++)
                sum+=arr[i][j];
        }
        System.out.println(sum);

        // 2. 남아있는 얼음 중 가장 큰 덩어리가 차지하는 칸의 개수 - BFS
        Queue<int[]> queue = new LinkedList<int[]>();
        boolean[][] isVisited = new boolean[boxSize][boxSize];
        int maxBlock = 0;
        for(int i=0; i<boxSize; i++) {
            for(int j=0; j<boxSize; j++) {
                int blocksize = 0;
                if(!isVisited[i][j]&&arr[i][j]>0) { // 확인하지 않은 얼음칸 확인
                    blocksize++;
                    isVisited[i][j] = true;
                    int[] pos = {i,j}; // 확인하지 않은 얼음칸 위치
                    queue.add(pos);
                    int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}};
                    while(!queue.isEmpty()) {
                        int[] curPos = queue.poll();
                        for(int d=0; d<4; d++) { // 인접 칸 탐색
                            int dx = curPos[0]+deltas[d][0];
                            int dy = curPos[1]+deltas[d][1];
                            if(dx>=0&&dx<boxSize&&dy>=0&&dy<boxSize&&!isVisited[dx][dy]&&arr[dx][dy]>0) { // 범위 내에 방문하지 않은 얼음칸을 방문한 경우
                                blocksize++;
                                isVisited[dx][dy] = true;
                                int[] newNode = {dx,dy};
                                queue.add(newNode); // 탐색 위치 큐에 넣고 다시 탐색
                            }
                        }
                    }
                }
                maxBlock = Math.max(maxBlock, blocksize);
            }
        }
        System.out.println(maxBlock);

    }
}