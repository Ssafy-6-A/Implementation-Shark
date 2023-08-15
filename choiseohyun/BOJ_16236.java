import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;



/* 아기상어
 * N*N크기 공간에 물고기 M마리와 상어 1마리(크기2)
 * 상어는 자기보다 작은 물고기만 먹으며 큰물고기는 못지나가고 같은크기는 지나갈수만있음
 * 먹을거 1마리면 먹으러가고 1보다 많으면 가까운곳(가장위,그중왼쪽)
 * 자기와 크기가 같은수의 물고기 먹을때마다 크기 +1
 * 엄마 부르지않고 몇초동안 돌아다닐지?
 * 
 * 풀이 : 가장 가까이있는 물고기를 찾기를 반복한다. 즉 bfs를 반복한다.
 * 		이때 depth를 저장하여 얼마나 멀리있는 물고기인지도 함께 기록한다.
 * 		물고기가 선택되면 전역변수로 공유되고있는 아기상어의 위치정보와 물고기 수 등을 업데이트한다.
 */
public class Main {
	static int[][] map;
	static int N,curX,curY,answer=0,curSize=2,curEaten=0,leftFish;
	static int[] dx = {-1,0,0,1};
	static int[] dy = {0,-1,1,0};


	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		N = Integer.parseInt(br.readLine());
		map = new int[N][N];
		leftFish = 0;
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
				if(map[i][j]==9) {
					curX = i;
					curY = j;
					map[curX][curY] = 0;
				}
				else if(map[i][j]>0) leftFish ++;
			}
		}
		int tmp;
		while(leftFish!=0) {
			tmp = leftFish; //탐색전 물고기 수 저장
			bfs();
			if(tmp==leftFish) break; //탐색후에 남은 물고기수가 똑같으면 종료(더이상 먹을수있는게 없단뜻)
		}
		System.out.println(answer);
	}


	private static void bfs() {
		boolean[][] visited = new boolean[N][N];
		Queue<int[]> q = new LinkedList<int[]>();
		q.offer(new int[]{curX,curY,1}); //아기상어 먼저 넣음
		visited[curX][curY] = true;

		int nextX=-1, nextY=-1, nextDepth=Integer.MAX_VALUE; //다음 먹을 물고기 위치와 시간 초기화

		//상어가 먹을 가장 가까운 물고기 한마리를 찾는다
		while(!q.isEmpty()) {
			int[] tmp = q.poll();
			int x = tmp[0];
			int y = tmp[1];
			int depth = tmp[2]; //depth:n칸이상의 탐색 즉 2이면 현재로부터 2 떨어진 거리 탐색
			//이전에 탐색해서 찾은 다음 물고기까지의 이동거리보다 현시점 탐색할 위치의 이동거리가 더 길면 의미없는 탐색이므로 탐색종료
			if(nextDepth<depth) break;

			for(int i=0; i<4; i++) {
				int nx = x+dx[i];
				int ny = y+dy[i];
				if(nx>=0 && nx<N && ny>=0 && ny<N 
						&& !visited[nx][ny] && map[nx][ny]<=curSize) {
					if(map[nx][ny]>0 && curSize>map[nx][ny]){//물고기가 현재 상어보다 작을때
						//다음 먹을 물고기가 정해지지 않았을때
						if(nextX==-1) {
							nextX = nx;
							nextY = ny;
							nextDepth = depth;
						} else { //다음 먹을 물고기가 정해져있을때
							//다음 먹을 물고기보다 현재 물고기가 더 위에있음 -> 변경
							if(nextX>nx) {
								nextX = nx;
								nextY = ny;
							}else if(nextX==nx) {
								//다음먹을 물고기보다 현재 물고기가 더 왼쪽에 위치 -> 변경
								if(nextY>ny) {
									nextX = nx;
									nextY = ny;
								}
							}
						}
					}
					visited[nx][ny] = true;
					q.offer(new int[] {nx,ny,depth+1});
				}
			}
		}
		//먹을 수 있는 물고기가 정해졌다면 처리해줌
		if(nextX!=-1) {
			map[nextX][nextY] = 0;
			answer += nextDepth;
			leftFish--;
			curX=nextX;
			curY=nextY;
			curEaten++;
			//크기up
			if(curEaten==curSize) {
				curEaten = 0;
				curSize++;
			}
		}
	}
}
