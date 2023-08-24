
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/* 상어초등학교
 * 교실은 N*N, 학생수는 N^2명, 각 학생은 1~N^2까지 번호 부여받음
 * 가장윗칸은 (1,1) 오른쪽아래는 (N,N)
 * 한칸엔 학생 한명만 앉으며, |r1-r2|+|c1-c2|=1인 두 칸이 (r1,c1)과 (r2,c2)를 인접한다고 한다
 * 
 * 1. 비어있는 칸 중에서 좋아하는 학생이 인접한 칸에 가장 많은 칸으로 자리 정함
 * 2. 1을 만족하는 칸이 여러개면 인접 칸 중에서 비어있는 칸이 가장 많은칸으로 자리 정함
 * 3. 2를 만족하는 칸도 여러개인 경우 행의번호가 가장 작은 칸으로, 그러한 칸도 여러개면 열번호가 가장 작은칸으로 정함
 * 
 * 자리배치가 끝난 후 만족도를 구한다. 인접한 칸에 좋아하는학생이 몇명 앉아있는지 구한다. 값이 0이면 0, 1이면 1, 2면 10, 3이먄 100, 4면 1000이다. 
 *
 * 풀이 : 우선순위 큐를 이용해 여러조건에서 정렬조건을 만들어준뒤 각 학생의 최적위치를 찾는다
 */
public class Main {
	static class Student implements Comparable<Student> {
		int x,y,cnt,emptyCnt;

		Student(int x, int y, int cnt, int emptyCnt){
			this.x = x;
			this.y = y;
			this.cnt = cnt;
			this.emptyCnt = emptyCnt;
		}

		// (0) 친한친구가 가장 많이 인접한곳
		// (1) (0)이 여러개면 빈칸이 더 많은 칸으로
		// (2) (1)도 여러개라면 행번호가 가장 작은칸, 이것도 여러개면 열번호가 작은칸
		@Override
		public int compareTo(Student o) {
			//인접한 친한친구의 수가 같다면 
			if(o.cnt == this.cnt) {
				//비어있는 자리수가 같다면
				if(o.emptyCnt == this.emptyCnt) {
					//행의 오름차순, 행이같다면 열의 오름차순
					if(o.x == this.x) return this.y-o.y;
					return this.x-o.x;
				}
				//비어있는 자리수의 내림차순
				return o.emptyCnt-this.emptyCnt;
			}
			// 디폴트는 친한친구의 내림차순
			return o.cnt-this.cnt;
		}
	}
	static int N, map[][], student[], dist[][] = {{1,0},{-1,0},{0,1},{0,-1}};
	static HashSet<Integer>[] list;

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		N = Integer.parseInt(br.readLine());
		map = new int[N][N];
		list = new HashSet[N*N+1];//각 학생의 친한친구목록을 저장 (student[1]의친한친구, student[2]의 친한친구..)
		student = new int[N*N+1];//학생번호를 순서대로 배열에 저장

		for(int i=1; i<N*N+1; i++) list[i] = new HashSet<>(); 
		for(int i=1; i<N*N+1; i++) {
			st = new StringTokenizer(br.readLine());
			int s = Integer.parseInt(st.nextToken());
			student[i] = s;
			for(int j=0; j<4; j++) list[s].add(Integer.parseInt(st.nextToken()));
		}

		map[1][1] = student[1]; //맨 처음 학생 셋팅
		for(int i=2; i<N*N+1; i++) setPosition(i); //두번째학생부터 자리배치

		System.out.println(getSatisfaction());
	}

	// student[s]의 학생의 자리를 배치하는 메소드 - 우선순위 큐를 이용해서 적절한 좌석을 찾는다.
	private static void setPosition(int s) {
		PriorityQueue<Student> pq = new PriorityQueue<Student>();
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) { //빈자리를 찾으면 해당 위치에서의 학생객체를 만들어 우선순위큐에 할당
				if(map[i][j] != 0) continue;
				pq.add(getStudent(i,j,s));
			}
		}

		//우선순위 큐의 가장 상단 객체가 최적자리이므로 저장
		int x = pq.peek().x;
		int y = pq.peek().y;
		map[x][y] = student[s];
	}

	// 학생 객체를 만들어주는 메소드 - 파라미터로 주어진 위치에서 사방탐색하여 친한친구가 있는지 찾은 뒤 생성한다.
	private static Student getStudent(int x, int y, int s) {
		int cnt = 0;
		int emptyCnt = 0;
		for(int i=0; i<4; i++) {
			int nx = x+dist[i][0];
			int ny = y+dist[i][1];
			if(nx<0 || ny<0 || nx>=N || ny>=N) continue;
			if(list[student[s]].contains(map[nx][ny])) cnt++;
			if(map[nx][ny] == 0) emptyCnt++;
		}
		return new Student(x, y, cnt, emptyCnt);
	}

	// 완성된 map을 토대로 만족도 계산
	private static int getSatisfaction() {
		int sum = 0;
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				int cnt = 0;
				for(int k=0; k<4; k++) {
					int nx = i+dist[k][0];
					int ny = j+dist[k][1];
					if(nx<0 || ny<0 || nx>=N || ny>=N) continue;
					if(list[map[i][j]].contains(map[nx][ny])) cnt++;
				}
				if(cnt==1) sum+=1;
				else if(cnt==2) sum+=10;
				else if(cnt==3) sum+=100;
				else if(cnt==4) sum+=1000;
			}
		}
		return sum;
	}
}
