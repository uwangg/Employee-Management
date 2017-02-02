import java.util.*;
import java.text.*;
import java.io.*;

class Employee  implements Cloneable {
    String name;		// 사원 이름
    int nameId;		// 사원 ID
    int job; 		//		0 : Staff
    //		1 : Developer
    //		2 : Tester
    boolean manabool;	// Manager = true, not false
    Employee nextEmployee;

    Employee() {
        nextEmployee = null;
    }
    Employee(String name, int nameId ,boolean manabool, int job) {
        this.name = name;
        this.nameId = nameId;
        this.manabool = manabool;
        this.job = job;
        this.nextEmployee = null;
    }
    void changeName(String name) {
        this.name = name;
    }
    void changeNameId(int nameId) {
        this.nameId = nameId;
    }
    void print() {
        System.out.println("[ " + name + "(" + nameId + ")" + " ]");
    }

    public Employee valueCopy(){

        Object obj = null;

        try{
            obj = clone();
        }catch(CloneNotSupportedException e){
            System.out.println("Can't copy the OBJECT");
        }

        return (Employee)obj; //형변환 시켜서 리턴해주면
    }
}

class Manager extends Employee {
    private String password="20000";
    Manager(String name, int nameId ,boolean manabool, int job) {
        super(name, nameId, manabool, job);
    }
    Manager() {
        super();
    }

    boolean chkPass( String pass) throws Exception
    {
        if( !password.equals(pass) ) throw new Exception("잘못된 암호입니다.");
        return true;
    }
    void setPass(String pass)
    {
        password = pass;
    }
    String getPass()
    {
        return password;
    }
}
abstract class EmployeeProgram {
    //LinkedList EmployeeArr = new LinkedList();
    Manager manager;
    abstract boolean insertEmployee(String name, int nameId, boolean manabool, int job);	// 직원추가
    abstract boolean insertEmployee(Employee newEmployee);
    abstract void deleteEmployee(String name, int nameId) throws Exception;;	// 직원삭제
    abstract void deleteEmployee(int locaiton) throws Exception;	// 직원삭제
    abstract void changeEmployee(String modname, String name, int nameId);	// 직원수정
    abstract LinkedList searchEmployee(String name);		// 직원이름으로검색
    abstract int searchIndexEmployee(int nameId);	 // 직원ID로 위치 검색
    abstract Employee get(int location);
    abstract int getSize();
    /*Employee searchEmployee(int nameId)	 // 직원ID로 검색
    {
        return EmployeeArr.search(nameId);
    }*/
    abstract void print();

}

class DevelopTeam extends EmployeeProgram {
    LinkedList SWDeveloper;
    LinkedList SWTester;
    //Employee manager;
    DevelopTeam(String name, int nameId, boolean manabool, int job) {
        SWDeveloper = new LinkedList();
        SWTester = new LinkedList();
        manager = new Manager(name, nameId, manabool, job);
        //SWDeveloper.add(manager, 0);
        //SWTester.add(manager, 0);
    }
    DevelopTeam() {
        SWDeveloper = new LinkedList();
        SWTester = new LinkedList();
    }
    public boolean insertEmployee(String name, int nameId, boolean manabool, int job)	// 직원추가
    {
        if(manabool == true)	// 매니저일경우 manager에 저장
        {
            Manager manager = new Manager(name, nameId, manabool, job);
        }
        else{
            if( job > 0 ) {			//Job > 0 -> Dveloper or Tester
                Employee newEmployee = new Employee(name, nameId, manabool, job);
                if( job == 1 ){
                    SWDeveloper.add(newEmployee);
                }
                else{
                    SWTester.add(newEmployee);
                }
            }
            else {
                System.out.println("잘못된 작업입니다.");
                return false;
            }
        }
        return true;
    }
    boolean insertEmployee(Employee newEmployee) {

        if(newEmployee.manabool == true)	// 매니저일경우 manager에 저장
        {
            manager = (Manager)newEmployee;
        }
        else{
            if( newEmployee.job > 0 ) {			//Job > 0 -> Dveloper or Tester
                if( newEmployee.job == 1 )
                    SWDeveloper.add(newEmployee);
                else
                    SWTester.add(newEmployee);
            }
            else {
                System.out.println("잘못된 작업입니다.");
                return false;
            }
        }
        return true;
    }
    public void deleteEmployee(String name,int nameId) throws Exception	// 직원삭제
    {
        if(SWTester.getSize() + SWDeveloper.getSize() == 1) throw new Exception("개발팀에는 최소 한명의 사원이 존재해야 합니다.");
        SWDeveloper.remove(SWDeveloper.searchInd(name,nameId));
        SWTester.remove(SWTester.searchInd(name,nameId));
    }
    void deleteEmployee(int location) throws Exception {
        if(SWTester.getSize() + SWDeveloper.getSize() == 1) throw new Exception("개발팀에는 최소 한명의 사원이 존재해야 합니다.");
        if(location <= SWDeveloper.getSize())
        {
            SWDeveloper.remove(location);
        }
        else
        {
            SWTester.remove(location-SWDeveloper.getSize());
        }
    }
    public void changeEmployee(String modname, String name, int nameId)	// 직원수정
    {
        SWDeveloper.modify(modname,SWDeveloper.searchInd(name,nameId));
        SWTester.modify(modname,SWTester.searchInd(name,nameId));
    }
    public LinkedList searchEmployee(String name)	// 직원이름으로검색
    {
        LinkedList Dev, Tes;

        Dev = SWDeveloper.search(name);
        Tes = SWTester.search(name);
        if(Dev == null ) Dev = new LinkedList();
        if(Tes == null ) Tes = new LinkedList();
        //Developer와 Tester에서 찾은 사원들의 리스트를 합병하여 반환
        Dev.merge(Dev, Tes);

        if( manager.name.equals(name)) Dev.add(manager);
        return Dev;
    }
    int searchIndexEmployee(int id) {

        int loc;


        //개발자에서 검색, -1이거나 size+1이면 검색결과 없음
        loc = SWDeveloper.searchI(id);
        if(loc == -1 || loc == SWDeveloper.getSize()+1) loc = 0;
        else if( manager.nameId == id) return -2;
        else return loc;

        //테스터에서 검색, -1이거나 size+1이면 검색결과 없음
        //검색결과 있으면 개발자 크기 + index값 반환
        loc = SWTester.searchI(id);
        if(loc == -1 || loc == SWTester.getSize()+1) return -1;
        else return SWDeveloper.getSize() + loc;

    }
    public void print()
    {
        System.out.println("manager : ");
        manager.print();
        System.out.println("SWDeveloper : ");
        SWDeveloper.print();
        System.out.println("SWTester : ");
        SWTester.print();
    }

    Employee get(int location) {
        if(location > SWDeveloper.getSize())
            location -= (SWDeveloper.getSize()+1);
        else return SWDeveloper.get(location);

        return SWTester.get(location);
    }
    int getSize() {
        return SWDeveloper.getSize() + SWTester.getSize();
    }
}

class Staff extends EmployeeProgram {
    //Employee manager;
    LinkedList staff;
    Staff(String name, int nameId, boolean manabool, int job) {
        staff = new LinkedList();
        manager = new Manager(name, nameId, manabool, job);
        //staff.add(manager, 0);
    }

    Staff() {
        staff = new LinkedList();
    }
    public boolean insertEmployee(String name, int nameId, boolean manabool, int job)	// 직원추가
    {
        if(manabool == true)	// 매니저일경우 manager에 저장
        {
            manager = new Manager(name, nameId, manabool, job);
        }
        else{
            if( job == 0 ) {			//Job == 0 -> Staff
                Employee newEmployee = new Employee(name, nameId, manabool, job);
                staff.add(newEmployee);
            }
            else {
                System.out.println("잘못된 작업입니다.");
                return false;
            }
        }
        return true;
    }
    boolean insertEmployee(Employee newEmployee) {
        if(newEmployee.manabool == true)	// 매니저일경우 manager에 저장
        {
            manager = (Manager)newEmployee;
        }
        else{
            if( newEmployee.job == 0 ) {			//Job == 0 -> Staff
                staff.add(newEmployee);
            }
            else {
                System.out.println("잘못된 작업입니다.");
                return false;
            }
        }

        return true;
    }
    public void deleteEmployee(String name, int nameId) throws Exception	// 이름&직원id로 직원삭제
    {
        if( staff.getSize() == 1 ) throw new Exception("최소 한명의 사원이 존재해야 합니다.");
        staff.remove(staff.searchInd(name,nameId));
    }
    public void deleteEmployee(int location) throws Exception			// 위치로 직원삭제
    {
        if( staff.getSize() == 1 ) throw new Exception("최소 한명의 사원이 존재해야 합니다.");
        staff.remove(location);
    }
    public void changeEmployee(String modname, String name, int nameId)	// 직원수정
    {
        staff.modify(modname,staff.searchInd(name,nameId));
    }
    public LinkedList searchEmployee(String name)	// 직원이름으로검색
    {
        LinkedList re;
        re = staff.search(name);
        if( re == null ) re = new LinkedList();
        if( manager.name.equals(name)) re.add(manager);
        return re;
    }
    int searchIndexEmployee(int nameId) {
        if( manager.nameId == nameId ) return -2;
        return staff.searchI(nameId);
    }
    public void print()
    {
        System.out.println("manager : ");
        manager.print();
        System.out.println("staff : ");
        staff.print();
    }
    public Employee get(int location) {
        return staff.get(location);
    }
    int getSize() {
        return staff.getSize();
    }

}

class LinkedList {
    private Employee head;
    private int size=0;

    // 노드 추가 함수
    public void add(Employee _newNode) {
        Employee newNode = _newNode.valueCopy();
        newNode.nextEmployee = null;
        if (head == null)
            head = newNode;
        else {
            Employee tail = head;
            while (tail.nextEmployee != null)
                tail = tail.nextEmployee;
            tail.nextEmployee = newNode;
        }
        size++;
    }       // 노드 추가 함수(지정된 위치)
    public void add(Employee _newNode, int location) {
        Employee newNode = _newNode.valueCopy();
        newNode.nextEmployee = null;

        // 헤드 위치에 삽입될 경우
        if (location == 0) {
            newNode.nextEmployee = head;
            head = newNode;
        }
        else {
            Employee before = head;
            // 이전 노드를 찾아 연결
            while ((--location) > 0)
                before = before.nextEmployee;
            newNode.nextEmployee = before.nextEmployee;
            before.nextEmployee = newNode;
        }
        size++;
    }
    public int searchInd(String name, int nameId) {	//이름, 직원id로 검색
        int cnt = 0;
        if (head == null)
            return -1;
        else {
            Employee tail = head;
            while (!tail.name.equals(name) || !(tail.nameId == nameId))
            {
                tail = tail.nextEmployee;
                cnt++;
                if(tail == null)
                    break;
            }
            return cnt;
        }
    }

    public int searchI(int nameId) {	//직원id로 검색
        int cnt = 0;
        if (head == null)
            return -1;
        else {
            Employee tail = head;
            while (tail.nameId != nameId )
            {
                tail = tail.nextEmployee;
                cnt++;
                if(tail == null)
                    break;
            }
            return cnt;
        }
    }

    public LinkedList search(String name) {	//이름으로 검색
        LinkedList result = new LinkedList();
        if (head == null)
            return null;
        else {
            Employee tail = head;
            while (tail != null)
            {
                if(tail.name.equals(name))
                    result.add(tail);
                tail = tail.nextEmployee;
            }
            return result;
        }
    }

    public Employee get(int location) {
        Employee current = head;
        while ((--location) >= 0)
            current = current.nextEmployee;
        return current;
    }
    // 노드 삭제
    public void remove(int location) {
        // 제거할 노드가 헤드이면
        if(location == 0)
            head = head.nextEmployee;
        else
        {
            Employee before = head;
            while ((--location) > 0)
                before = before.nextEmployee;
            Employee after = before.nextEmployee.nextEmployee;
            if (after != null)
                before.nextEmployee = after;
            else
                before.nextEmployee = null;
        }
    }
    // 노드 수정
    public void modify(String name, int location) {
        Employee current = head;

        if(location > 0)
        {
            while ((--location) >= 0)
                current = current.nextEmployee;
        }
        current.changeName(name);
    }
    // 노드 정보 출력
    public void print() {
        StringBuffer sb = new StringBuffer();
        Employee current = head;
        int size = 0;
        while (current != null) {
            sb.append(current.name);
            sb.append("(");
            sb.append(current.nameId);
            sb.append("), ");
            current = current.nextEmployee;
            size++;
        }
        if(size == 0)
        {
            System.out.println("[ ]");
            System.out.println("size : " + size);
            return;
        }
        int i = sb.lastIndexOf(", ");
        sb.delete(i, i + 2);
        System.out.println("[ " + sb + " ]");
        System.out.println("size : " + size);
    }

    ///상황봐서 추후 삭제. id와 함게 깔끔하게 출력하기 위해 만들었으나 중복되는거 같음
    public void printWithId() {
        StringBuffer sb = new StringBuffer();
        int cnt=0;
        Employee current = head;
        while (current != null) {
            sb.append(++cnt + ". " + current.name);
            sb.append("\t");
            sb.append(current.nameId);
            sb.append("\n");
            current = current.nextEmployee;
        }
        System.out.println(sb);
    }
    public int getSize() {
        return size;
    }
    public void merge(LinkedList srcNode, LinkedList desNode) {			//두 리스트를 합친다. ( src의 마지막 노드가 가진 nextEmployee에 des의 헤더를 대입 )
        Employee tmp;
        tmp = srcNode.head;
        if(tmp != null) {
            while(tmp.nextEmployee != null) tmp = tmp.nextEmployee;
            tmp.nextEmployee = desNode.head;
        }
        else
            tmp = desNode.head;
    }
}

class DateFormat{  // 날짜 출력클래스

    public static String date(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd    HH:mm , ");
        String str = dateFormat.format(calendar.getTime());
        return str;   // 리턴값 	ex)  2012.12.11   14:22  ,
    }
}

public class Main
{
    public static void main(String[] args)
    {
        String buffer;
        int menu = -1;
        int c;
        Scanner scn = new Scanner(System.in);
/*
		DevelopTeam dev1 = new DevelopTeam("개발1팀매니저",0,true,1);	// 개발1팀
		DevelopTeam dev2 = new DevelopTeam("개발2팀매니저",0,true,1);	// 개발2팀
		DevelopTeam dev3 = new DevelopTeam("개발3팀매니저",0,true,1);	// 개발3팀
		Staff secretary = new Staff("비서팀매니저",0,true,0);	// 비서팀
		Staff affairs = new Staff("총무팀매니저",0,true,0);	// 총무팀
		Staff resource = new Staff("인사팀매니저",0,true,0);	// 인사팀
*/
        DevelopTeam dev1 = new DevelopTeam();	// 개발1팀
        DevelopTeam dev2 = new DevelopTeam();	// 개발2팀
        DevelopTeam dev3 = new DevelopTeam();	// 개발3팀
        Staff secretary = new Staff();	// 비서팀
        Staff affairs = new Staff();	// 총무팀
        Staff resource = new Staff();	// 인사팀
        EmployeeProgram arr[] = {secretary, affairs, resource, dev1, dev2, dev3};
        String TeamName[] = { "비서실", "총무팀", "인사팀 ", "개발1팀", "개발2팀", "개발3팀 " };
        String text[]={"신규직원 추가","기존직원 삭제","기존직원 수정","이름으로 검색","부서명으로 검색","매니저명으로 검색","부서이동"}; // 입력변수

        //파일 읽기
        ReadFile(arr);


        while(menu != 7)
        {
            System.out.println("------------menu------------");
            System.out.println("(1) 신규 직원 추가");
            System.out.println("(2) 기존 직원 삭제 및 수정");
            System.out.println("(3) 직원 검색");
            System.out.println("(4) 부서 이동");
            System.out.println("(5) 히스토리");
            System.out.println("(6) 파일 저장");
            System.out.println("(7) 종료");
            int n=0;
            System.out.println("원하는 메뉴의 번호 >> ");
            try {
                n = scn.nextInt();	// 메뉴입력
            }
            catch(Exception e) {
                System.out.println("잘못 입력하셨습니다.");
                buffer = scn.nextLine();
                continue;
            }//try catch ok
            if(n == 1)	// (1) 신규 직원 추가
            {
                String newName;
                int newNameId=-1;
                buffer = scn.nextLine();

                System.out.println("추가하고자 하는 직원의 정보를 입력");
                System.out.print("직원 이름 : ");
                newName = scn.next();
                System.out.print("직원 Id : ");

                try {
                    newNameId = scn.nextInt();
                }
                catch(Exception e) {
                    System.out.println("잘못 입력하셨습니다.");
                    buffer = scn.nextLine();
                }//try catch ok


                for(c=0;c<6;c++) {
                    //System.out.println("c: "+c+" ind: "+arr[c].searchIndexEmployee(newNameId));
                    if (arr[c].searchIndexEmployee(newNameId)==-2 || (arr[c].searchIndexEmployee(newNameId)>=0
                            && arr[c].searchIndexEmployee(newNameId) < arr[c].getSize())) {
                        System.out.println("중복된 id입니다.");
                        break;
                    }
                }
                if( c != 6 ) continue;



                System.out.println("추가하고자 하는 개발팀을 고르세요.");
                System.out.println("(1) 비서실 (2) 총무팀 (3) 인사팀 (4) 개발1팀 (5) 개발2팀 (6) 개발3팀");
                try{
                    int n1;
                    n1 = scn.nextInt();

                    if(n1 <= 3)	// 이름,직원id,매니저확인,직업을 입력받아 추가
                        arr[n1-1].insertEmployee(newName, newNameId, false, 0);
                    else
                        arr[n1-1].insertEmployee(newName, newNameId, false, 1);
                    arr[n1-1].print();

                    //text[0]="신규직원 추가"
                    log_writer(text[0],newNameId);
                }
                catch (Exception e) {
                    System.out.println("잘못 입력하셨습니다.");
                    buffer = scn.nextLine();
                } //try catch ok
            }
            else if(n == 2)	// (2) 기존 직원 삭제 및 수정
            {
                System.out.println("삭제 혹은 수정하고자 하는 개발팀을 고르세요.");
                System.out.println("(1) 비서실 (2) 총무팀 (3) 인사팀 (4) 개발1팀 (5) 개발2팀 (6) 개발3팀");
                System.out.print(" >> ");
                int n2=0;
                String rmName;
                int rmNameId;
                int a;
                try {
                    n2 = scn.nextInt();
                    arr[n2-1].print();
                }
                catch (Exception e) {
                    System.out.println("잘못 입력하셨습니다.");
                    buffer = scn.nextLine();
                    continue;
                }//try-catch ok! if error->quit
                System.out.print("직원 이름 : ");
                rmName = scn.next();
                System.out.print("직원 ID : ");
                rmNameId = scn.nextInt(); //try catch? 아무튼 직원 id 중복 안되게 해야함

                System.out.println("(1) 직원 수정 (2) 직원 삭제");
                System.out.print(" >> ");
                try {
                    a = scn.nextInt();
                }
                catch (Exception e) {
                    System.out.println("잘못 입력하셨습니다.");
                    buffer = scn.nextLine();
                    continue;
                } //try catch ok
                if(a == 1)			//수정
                {
                    String modname;
                    System.out.print("수정하고자 하는 이름 : ");
                    modname = scn.next();
                    arr[n2-1].changeEmployee(modname, rmName, rmNameId);
                    //text[1]="기존직원 수정"
                    log_writer(text[2],rmNameId);
                } else if(a == 2)	//삭제
                {
                    try {
                        arr[n2-1].deleteEmployee(rmName, rmNameId);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    //text[1]="기존직원 삭제"
                    log_writer(text[1],rmNameId);
                } else {
                    System.out.println("잘못된 번호입니다.");
                    continue;
                }

                // 기존 직원이 아무것도 추가가 안되어있을 때 null pointer exception 처리 해주어야함
                // 해당하는 직원이 없는 상태에서 실행할 경우 null pointer exception 발생
                // 메모장에서 이미 읽어올 경우.. 없는 이름을 입력했을 경우에 대해서만 exception 처리

            }
            else if(n == 3)	// (3) 직원 검색
            {

                System.out.println("(1) 이름으로 검색 (2) 부서명으로 검색 (3) Manager명으로 검색");
                int s = 0;
                int n3 = 0;

                try {
                    s = scn.nextInt();
                }
                catch (Exception e) {
                    System.out.println("잘못 입력하셨습니다.");
                    buffer = scn.nextLine();
                    continue;
                }

                if( s == 3 ) {
                    for(int j=0;j<6;j++) System.out.println( TeamName[j] + " : " + arr[j].manager.name);
                }
                else if (s==1 || s==2) {
                    System.out.println("검색하고자 하는 개발팀을 고르세요.");
                    System.out.println("(1) 비서실 (2) 총무팀 (3) 인사팀 (4) 개발1팀 (5) 개발2팀 (6) 개발3팀");
                    try {
                        n3 = scn.nextInt();
                    }
                    catch (Exception e) {
                        System.out.println("잘못 입력하셨습니다.");
                        buffer = scn.nextLine();
                        continue;
                    }//try-catch ok! if error->quit
                }

                if(s == 1)	// 이름으로 검색할경우
                {
                    String sname;
                    System.out.print("이름 : ");
                    sname = scn.next();
                    arr[n3-1].searchEmployee(sname).print();
                    // 만약 검색대상이 이름이라면 text[3]
                    log_writer(text[3],sname);
                }
                else if(s == 2)	// 부서명으로 검색
                {
                    arr[n3-1].print();
                    // 만약 검색대상이 부서명이라면 text[4]
                    log_writer(text[4],TeamName[n3-1]);
                }
                else if( s == 3 ) // manager 명으로 검색
                {
                    System.out.print("매니저의 이름 : ");
                    String mname;
                    mname = scn.next();
                    for(int j=0;j<6;j++) {
                        if( arr[j].manager.name.equals(mname) )
                            arr[j].print();
                    }
                    // 만약 검색대상이 매니저명이라면 text[5]
                    log_writer(text[5],mname);
                }
                else {
                    System.out.println( "잘못된 입력입니다.");
                    continue;
                }


            }
            else if(n == 4)	// (4) 부서이동
            {
                int location = 0, s;
                int src, des;
                int id = 0;
                String name = null, pass = null;

                System.out.println("검색하고자 하는 개발팀을 고르세요.");
                System.out.println("(1) 비서실 (2) 총무팀 (3) 인사팀 (4) 개발1팀 (5) 개발2팀 (6) 개발3팀");
                src = scn.nextInt();

                if( arr[src-1].getSize() == 1 )
                {
                    System.out.println("해당 부서에는 최소 한명의 사원이 존재해야합니다.");
                    continue;
                }

                //권한 얻기
                System.out.println("패스워드를 입력해주세요");
                pass = scn.next();
                try {
                    if( arr[src-1].manager.chkPass(pass) ) {}
                } catch (Exception e) {
                    System.out.println( e.getMessage() );
                    continue;
                }//try-catch ok

                System.out.println("(1) 이름으로 검색 (2) ID로 검색 ");
                s = scn.nextInt();

                if(s == 1)	// 이름으로 검색할경우
                {
                    LinkedList re;
                    String sname;
                    System.out.print("이름 : ");
                    sname = scn.next();
                    re = arr[src-1].searchEmployee(sname);

                    System.out.println(re.getSize() + "개의 검색결과가 있습니다.");
                    re.printWithId();
                    if(re.getSize() == 0) continue;
                    System.out.println("선택할 직원의 번호를 입력해 주십시오 (1 ~ " + re.getSize() + ") ... ");

                    location = scn.nextInt();
                    id = re.get(--location).nameId;
                    name = re.get(location).name;
                    location = arr[src-1].searchIndexEmployee(id);
                }
                else if(s == 2)	// ID로 검색할경우
                {
                    System.out.print("id: ");
                    id = scn.nextInt();
                    location = arr[src-1].searchIndexEmployee(id);
                }
                else
                {
                    System.out.println("잘못된 입력입니다.\n");
                    continue;
                }
                System.out.println(location);
                if(location == -2)
                {
                    System.out.println("매니저는 부서의 이동이 불가능합니다.");
                    continue;
                }

                System.out.println("목적 팀을 고르세요.");
                System.out.println("(1) 비서실 (2) 총무팀 (3) 인사팀 (4) 개발1팀 (5) 개발2팀 (6) 개발3팀");
                des = scn.nextInt();
                if( arr[des-1].insertEmployee(arr[src-1].get(location)) )
                    try {
                        if( src <= 3) arr[src-1].deleteEmployee(location);			//Staff
                        else arr[src-1].deleteEmployee(location);			//Dev		.. 삭제에 이름과 id 필요
                    } catch( Exception e )
                    {
                        System.out.println(e.getMessage());
                        try {
                            arr[des-1].deleteEmployee(name, id);
                        } catch (Exception e1) {
                            System.out.println("부서 이동중 오류가 발생하였습니다.");
                        }
                        continue;
                    }


                //text[6]="부서이동"
                log_writer(text[6],id);
            }
            else if(n == 5)	// (5) 히스토리
            {
                log_reader();
            }
            else if(n == 6)	// (6) 파잏 저장
            {
                SaveInfo(arr);
            }
            else if(n == 7)	// (7) 종료
            {
                System.out.println("프로그램을 종료합니다.");
                System.exit(0);
            }
            else{
                System.out.println("잘못 입력하셨습니다.");
            }

        }
    }
    //log 파일 쓰기 함수
    static void log_writer(String text,int nameID){

        String date=DateFormat.date();
        BufferedWriter writer = null;

        try {
            //파일이 없으면 새로 만들고, 있다면 이어서 쓰기
            // log.txt 에 로그파일저장
            writer = new BufferedWriter( new FileWriter("log.txt",true));
            writer.write(date+text+"직원ID("+nameID+")");
            writer.newLine(); //개행문자
        }
        catch (IOException ioe) {
            System.out.println("파일로 출력할 수 없습니다.");
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
            }
        }
    }
    //log 파일 쓰기 함수  -  중복함수
    static void log_writer(String text,String value){

        String date=DateFormat.date();
        BufferedWriter writer = null;

        try {
            //파일이 없으면 새로 만들고, 있다면 이어서 쓰기
            // log.txt 에 로그파일저장
            writer = new BufferedWriter( new FileWriter("log.txt",true));
            writer.write(date+text+" : "+value);
            writer.newLine(); //개행문자
        }
        catch (IOException ioe) {
            System.out.println("파일로 출력할 수 없습니다.");
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
            }
        }
    }

    //log 파일 읽기 함수
    static void log_reader(){

        String buf;
        BufferedReader reader =null;

        try{
            reader = new BufferedReader( new FileReader("log.txt"));
            while(  (buf = reader.readLine())!= null ){
                System.out.println(buf);
            }
        }
        catch (IOException ioe){
            System.out.println("파일을 읽을 수 없습니다.");
        }
        finally{
            try{
                reader.close();
            }
            catch(Exception e){
            }
        }
    }

    static void ReadFile(EmployeeProgram arr[])
    {
        String buf;
        String line;
        BufferedReader reader = null;

        try{
            reader = new BufferedReader( new FileReader("empinfo.txt") );
            while(  (line = reader.readLine() )!= null ){
                StringTokenizer token = new StringTokenizer(line);
                String name = token.nextToken();
                int id = Integer.parseInt(token.nextToken());
                int dep = Integer.parseInt(token.nextToken());
                boolean manager = Boolean.parseBoolean(token.nextToken());
                int job = Integer.parseInt(token.nextToken());
                Manager newEmployee = new Manager(name, id , manager, job);

                if(manager) {
                    String pass = token.nextToken();
                    newEmployee.setPass(pass);
                    arr[dep].insertEmployee(newEmployee);
                }
                else {
                    arr[dep].insertEmployee( (Employee)newEmployee );
                }
            }
        }
        catch (IOException ioe){
            System.out.println("파일을 읽을 수 없습니다. " + ioe.getMessage());
        }
        finally{
            try{
                reader.close();
            }
            catch(Exception e){
            }
        }
    }

    static void SaveInfo(EmployeeProgram[] arr)
    {
        BufferedWriter writer = null;
        try{


            writer = new BufferedWriter( new FileWriter("empinfo.txt") );

            for(int i=0;i<6;i++)	//매니저
            {
                writer.write(arr[i].manager.name+" ");
                writer.write("" + arr[i].manager.nameId + " ");
                writer.write("" + i + " ");
                writer.write("True ");
                writer.write("" + arr[i].manager.job + " ");
                writer.write(arr[i].manager.getPass() + "\r\n");
            }


            for(int i=0;i<3;i++)	//개발지원팀
            {
                Employee node = arr[i].get(0);
                while( node != null )
                {
                    writer.write(node.name + " ");
                    writer.write("" + node.nameId + " ");
                    writer.write("" + i + " ");
                    writer.write("False ");
                    writer.write("" + node.job + "\r\n");
                    node = node.nextEmployee;
                }

            }
            for(int i=3;i<6;i++)	// 개발팀
            {
                int count = 0;
                Employee node = arr[i].get(count++);
                while( node != null )
                {
                    count++;
                    writer.write(node.name + " ");
                    writer.write("" + node.nameId + " ");
                    writer.write("" + i + " ");
                    writer.write("False ");
                    writer.write("" + node.job + "\r\n");
                    node = node.nextEmployee;

                }
                node = arr[i].get(count);
                while( node != null )
                {
                    writer.write(node.name + " ");
                    writer.write("" + node.nameId + " ");
                    writer.write("" + i + " ");
                    writer.write("False ");
                    writer.write("" + node.job + "\r\n");
                    node = node.nextEmployee;

                }

            }


            writer.close();
        }catch(IOException e)
        {
            try {
                System.out.println(e.getMessage());
                writer.close();
            } catch (IOException e1) {

            }
        }


    }

    public static boolean chkExistId(EmployeeProgram[] arr, int id)
    {
        for(int i=0;i<6;i++)
            if (arr[i].searchIndexEmployee(id)==0) {
                System.out.println("중복된 id입니다.");
                return false;
            }
        return true;
    }

}
