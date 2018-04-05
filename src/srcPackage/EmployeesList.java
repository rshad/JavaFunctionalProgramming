package srcPackage;

import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/** srcPackage.EmployeesList class, used to implement different functionalities, related to the employees
 * data file, like repairing the lost data, extracting data and store data of each employee.
 */
public class EmployeesList {

    /* { class data members } */
    private static Pattern patternSpace=Pattern.compile("\\s+");

    /* Class member, used to store the employees data as those appear in the file data.txt */
    private ArrayList<Employee> listFile = new ArrayList<>();

    /* Class member, used to store the employees as map with pair <dni - employee>
     * dni: Documento Nacional de Identificacion -> Identification National Document
     *
     * Note: This process will be done, once we repair the data file.
     */
    private Map<String, Employee> employee_map = new HashMap<>();


    /* Additional class data members */

    /*
     * The following data structures are created to store the stored information in the
     * sectores and route files. So we can faciltate the access to this information with-
     * out the need to open the corresponding file on each time we make use of this inf-
     * ormation.
     *
     * So we have the following:
     *  sector1_set : corresponds to the file assignmentSECTOR1
     *  sector2_set : corresponds to the file assignmentSECTOR2
     *
     *  route1_set : corresponds to the file assignmentROUTE1
     *  route2_set : corresponds to the file assignmentROUTE2
     *  route3_set : corresponds to the file assignmentROUTE3
     *
     * */

    //Sectors ...
    private Set<String> sector1 = new HashSet<>();
    private Set<String> sector2 = new HashSet<>();
    private Set<String> no_sector = new HashSet<>();

    //Routes ...
    private Set<String> route1 = new HashSet<>();
    private Set<String> route2 = new HashSet<>();
    private Set<String> route3 = new HashSet<>();
    private Set<String> no_route = new HashSet<>();





    /* Auxiliar methods */
    private void Read_File_And_Create_Set(String file_path, Set<String> Set_Parameter_to_Store){
        try(BufferedReader br = new BufferedReader(new FileReader(file_path))) {

            br.readLine(); //Skipping the first line, the sector or route name ...
            br.readLine(); //Skipping the second line, a blank line ...

            for (String line; (line = br.readLine()) != null; ) {
                Set_Parameter_to_Store.add(line); //storing the ID ...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* { constructors and functions members } */

    /**
     * srcPackage.EmployeesList(), is the srcPackage.main constructor
     */
    public EmployeesList(){

    }

    /**
     * srcPackage.EmployeesList( String data-file-path ), is a secondary constructor, describe the srcPackage.main
     * functionality of the cosntructor of {@link EmployeesList} class.
     *
     * The srcPackage.main object of this constructor is processing the data file's content, so in the end
     * the class data member listFile will have stored all the srcPackage.Employee class objects. (One per
     * each line of the file).
     *
     * This constructor receives as an aregument, the data file path, then it supposed to impl-
     * ement and store all the objects of the class {@link Employee}. Those objects which are
     * necessary in the class data memeber listFile.
     *
     * These objects are maintained in this structure till the moment in which all are repaired.
     *
     * Once are repaired, the class data member, the dictionary employee_map will be generated.
     *
     *
     * @param data_file_path, String, represents the path of the data file which co-
     *                        ntains the related data for each employee.
     */
    public EmployeesList(String data_file_path) throws InterruptedException {
        /* { local varaibles's declaration ... } */
        /*
         * local variable, used to temporary store the result of processing each line of the
         * of data_file_path when we call CreateEmployee function.
         */
        Employee processed_line;


        /* { proceeding ... } */
        int i = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(data_file_path))) {
            for(String line; (line = br.readLine()) != null; ) {

                /*
                 * Processing line by calling CreateEmployee function to create the
                 * corresponding srcPackage.Employee object.
                 *
                 **/
                processed_line = this.CreateEmployee(line);

                /*
                 * Once we processed the line, and convert it to srcPackage.Employee object, we store it in the
                 * class data member listFile.
                 **/
                this.listFile.add(processed_line);

                /* { Reapir the line .... } */

                /*
                 * Once we process the line, we get an srcPackage.Employee object. Then we store this object
                 * as the value with the corresponding dni as key, in the class data member, employee_map.
                 **/
                this.employee_map.put(processed_line.obtenerDni(),processed_line);
            }
            // line is not visible here.
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating threads to make a parallel execution ..
        Thread thread1 = new Thread(() -> Read_File_And_Create_Set(GlobalCommonVaraibles.sector1_path,sector1));
        Thread thread2 = new Thread(() -> Read_File_And_Create_Set(GlobalCommonVaraibles.sector2_path,sector2));

        Thread thread3 = new Thread(() -> Read_File_And_Create_Set(GlobalCommonVaraibles.route1_path,route1));
        Thread thread4 = new Thread(() -> Read_File_And_Create_Set(GlobalCommonVaraibles.route2_path,route2));
        Thread thread5 = new Thread(() -> Read_File_And_Create_Set(GlobalCommonVaraibles.route3_path,route3));

        // Starting the execution ...
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        // Waiting for them to finish ...
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();

    }

    /**
     * CreateEmployee(String line-of-file) used to make the required analysis of each line of
     * the data file, and once it's analayzed it extracts the basic necessary information to
     * call the constructor of the class {@link Employee}.
     *
     * @param line_of_file, String, represents a file of the data file. this line corresponds to
     *                      an employee.
     *
     * @return line_employee, {@link Employee}, used to store the different elements of a line entry
     */
    Employee CreateEmployee(String line_of_file){

        /* { declaration of local variables ... } */

        /* line_elements, is a local variable. used to store the extracted data of line_of_file */
        ArrayList<String> line_elements = new ArrayList<>();
        /* line_employee, is a local variable. Represents the return value of this function. */
        Employee line_employee;
        // lineScanner, used to create a Scanner object of line_of_file
        Scanner lineScanner = new Scanner(line_of_file);


        /* { proceeding ... } */

        // making ',  ' as a delimiter
        String Delimiter = ",  ";
        lineScanner.useDelimiter(Delimiter);
        // extracting the elements, stored in lineScanner
        while (lineScanner.hasNext()) {
            // store each splitted data from the Scanner object.
            line_elements.add(lineScanner.next());
        }

        /*
         * Once we have the line's elements stored in line_elements, we've to store them in an
         * srcPackage.Employee object, line_employee, to be returned as a result of CreateEmployee.
         *
         * As we can see, we store the elements in a secuential srcPackage.Route, so we can establish a dete-
         * mined mechanism to extract them and store each element in its corresponding element of
         * line_employee. For example, in this case we know that line_elements[0] is (dni) ...
         */

        // Assigning the elements values to line_employee by making use of the corresponding constructor
        line_employee = new Employee(
                line_elements.get(0), //Assigning the element {dni -> IND}
                line_elements.get(1), //Assigning the element {nombre -> name}
                line_elements.get(2), //Assigning the element {apellido -> last_name}
                line_elements.get(3)  //Assigning the element {correo -> email}
        );

        //Finally, we return line_employee
        return line_employee;
    }

    /**
     * CountEmployeesInFile, is a function, used to count the employees in data_file_path.
     * We have 2 alternatives to implement this function, facilities of functional programming or
     * directly return the size of the class data member, employee_map.
     *
     * We gonna proceed, using the second alternatives.
     *
     * @return employee_map.size() , int, represents the number of the employees in the class data member
     *                              employee_map.
     **/
    public int CountEmployeesInFile(){
        return(this.listFile.size());
    }

    /**
     * getDuplicatesDni, is a function, used to get the Employees with the same ID.
     *
     * @return res, Map<String,List<srcPackage.Employee>, a map with dni as key and list
     *              of employees whos share this id.
     *
     * */
    public Map<String, List<Employee>> getDuplicatesDni(){

        /*
         * Firstly, we create the Map of the dni as key and the list of employees who share this dni.
         *
         * Proceeding:
         * -----------
         *      1) We call the stream() of employee_list.
         *      2) We call collect(Collectors.groupingBy(srcPackage.Employee::obtenerDni)) to group the Employees
         *         who share the same dni in a list.
         */
        Map<String, List<Employee>> res = listFile.stream()  // Returns a sequential {@code Stream} with this collection as its source.
                .collect(Collectors.groupingBy(Employee::obtenerDni)); // group the employees by dni field

        /*
         * Once we hace the map, we call the stream() on entrySet() of the map. To filter the stream, we
         * call filter(map -> map.getValue().size() > 1) to only get the pair <dni,List<srcPackage.Employee>> with
         * List<srcPackage.Employee>.size() > 1. So we get the employees with repeated dni.
         */
        res = res.entrySet().stream()
                .filter(map -> map.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return res;
    }

    /**
     * countDuplicatesDni, is a function, used to count the duplicates in dni feild.
     *
     * @return duplicates, int, the number of duplicates.
     * */
    public int countDuplicatesDni(){
        // return the result -> true if there are duplicates.
        int sum = 0;

        sum = getDuplicatesDni().values().stream()// now it's a stream of List<Employee>
                .filter(Objects::nonNull) // remove lists that are null
                .mapToInt(List::size) // stream of list sizes
                .sum();

        return(sum);
    }

    /**
     * detectDuplicatesDni, is a function, used to detect if there are duplicates in dni field.
     * In this function we simply make a conditional evaluation over the output of the function
     * countDuplicatesDni.
     *
     * @return true or false, boolean, true if there are duplicates, false in other case.
     * */
    public boolean detectDuplicatesDni(){
        return( this.countDuplicatesDni() > 0 );
    }


    /**
     * RepairRepeatedDNIs, is a function, used to repair the repeated DNI's.
     *
     * To make this possible, we gonna use a member function of the class {@link Employee}
     * asignarDniAleatorio() -> "AssignRandomDNIs()".
     *
     * @param EmployeesSameDNI_List, Map<String, List<srcPackage.Employee>>, contains the list of employees
     *                               who share the same dni.
     **/
    public void RepairRepeatedDNIs(Map<String, List<Employee>> EmployeesSameDNI_List){
        String aux_ID = "";

        for(List<Employee> list_IDs: EmployeesSameDNI_List.values()){
            for(Employee emp:list_IDs){
                aux_ID = emp.obtenerDni();
                emp.asignarDniAleatorio();
                employee_map.put(emp.obtenerDni(),emp);
                employee_map.remove(aux_ID);
            }
        }
    }



    /* {Email related methods: Detection of anomalies, repair errors, ....} */
    /**
     * getDuplicatesEmails, is a function, used to get the Employees with the same ID.
     *
     * @return res, Map<String,List<srcPackage.Employee>, a map with Emails as key and list
     *              of employees whos share this id.
     *
     * */
    public Map<String, List<Employee>> getDuplicatesEmails(){

        /*
         * Firstly, we create the Map of the Emails as key and the list of employees who share this Emails.
         *
         * Proceeding:
         * -----------
         *      1) We call the stream() of employee_list.
         *      2) We call collect(Collectors.groupingBy(srcPackage.Employee::obtenerEmails)) to group the Employees
         *         who share the same Emails in a list.
         */
        Map<String, List<Employee>> res = listFile.stream()  // Returns a sequential {@code Stream} with this collection as its source.
                .collect(Collectors.groupingBy(Employee::obtenerCorreo)); // group the employees by Emails field

        /*
         * Once we hace the map, we call the stream() on entrySet() of the map. To filter the stream, we
         * call filter(map -> map.getValue().size() > 1) to only get the pair <Emails,List<srcPackage.Employee>> with
         * List<srcPackage.Employee>.size() > 1. So we get the employees with repeated Emails.
         */
        res = res.entrySet().stream()
                .filter(map -> map.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        return res;
    }

    /**
     * countDuplicatesEmails, is a function, used to count the duplicates in Emails feild.
     *
     * @return duplicates, int, the number of duplicates.
     * */
    public int countDuplicatesEmails(){
        // return the result -> true if there are duplicates.
        int sum = 0;

        sum = getDuplicatesEmails().values().stream()// now it's a stream of List<Employee>
                .filter(Objects::nonNull) // remove lists that are null
                .mapToInt(List::size) // stream of list sizes
                .sum();

        return(sum);
    }

    /**
     * detectDuplicatesEmails, is a function, used to detect if there are duplicates in Emails field.
     * In this function we simply make a conditional evaluation over the output of the function
     * countDuplicatesEmails.
     *
     * @return true or false, boolean, true if there are duplicates, false in other case.
     * */
    public boolean detectDuplicatesEmails(){
        return( this.countDuplicatesEmails() > 0 );
    }


    /**
     * RepairRepeatedEmailss, is a function, used to repair the repeated Emails's.
     *
     * To make this possible, we gonna use a member function of the class {@link Employee}
     * asignarEmailsAleatorio() -> "AssignRandomEmailss()".
     *
     * @param EmployeesSameEmails_List, Map<String, List<srcPackage.Employee>>, contains the list of employees
     *                               who share the same Emails.
     **/
    public void RepairRepeatedEmails(Map<String, List<Employee>> EmployeesSameEmails_List){ }


    /* The end of IDs and Emails, errors detection, counting .. ,reparation */

    /* Validating the information ... */
    public void validateFileList(){
        Map<String, List<Employee>> duplicates_dni_map = getDuplicatesDni();
        RepairRepeatedDNIs(duplicates_dni_map);

    }



    /**
     * process_sector_assigning, is a function, used to detect if line which is represented by a ID,
     * exists or not in the class data member, employee_map.
     *
     * @param line, a line of a sector file. normally is a one word, the ID.

     * @param sector, the sector for which the employee belongs.
     *
     * @param sector
     * @return boolean value, true if the ID (represented by line) exists in the class data member, employee_map
     *                        and false in other cases.
     */
    private boolean process_sector_assignment(String line, Sector sector){
        List<String> infos = patternSpace.splitAsStream(line).
        collect(Collectors.toList());

        boolean res = this.employee_map.get(infos.get(0)) != null;

        if(res){
            employee_map.get(line).asignarSector(sector);
        }

        return (res);

    }

    private boolean process_route_assignment(String line, Route route){
        List<String> infos = patternSpace.splitAsStream(line).
                collect(Collectors.toList());

        boolean res = this.employee_map.get(infos.get(0)) != null;

        if(res){
            employee_map.get(line).asignarRuta(route);
        }

        return (res);
    }

    private Sector ProcessSectorName(String line){
        List<String> infos = patternSpace.splitAsStream(line).
            collect(Collectors.toList());

        Predicate<Sector> condicion = sector ->
                (sector.name().equals(infos.get(0)));

        Sector sectorResultado = Arrays.stream(Sector.values()).
        filter(condicion).
        findFirst().get();

        return sectorResultado;
    }

    /**
     * LoadArchive_SectorAssigning, is a function, used to count the errors in the process of assigning
     * an employee to a sector. So it reads the file SectorFilePath, and for each read ID from the sector
     * file, it detects if this ID exists in
     *
     * @param SectorFilePath, represents the sector file's path.
     * */
    public int LoadArchive_SectorAssignment(String SectorFilePath) throws IOException {
        List<String> lineas = Files.lines(Paths.get(SectorFilePath)).
        collect(Collectors.toList());

        Sector sector = ProcessSectorName(lineas.get(0));

        long errores=lineas.stream().skip(2).
            map(linea -> process_sector_assignment(linea,sector)).
            filter(flag -> !flag).count();

        return (int) errores;

    }


    private Route ProcessRouteName(String line){
        List<String> infos = patternSpace.splitAsStream(line).
                collect(Collectors.toList());

        Predicate<Route> condicion = _route ->
                (_route.name().equals(infos.get(0)));

        Route routeResultado = Arrays.stream(Route.values()).
                filter(condicion).
                findFirst().get();

        return routeResultado;
    }

    //returns the number of errors as a cosnequence of the assigning.
    public int LoadArchive_RouteAssignment(String RouteFileName) throws IOException {
        List<String> lineas = Files.lines(Paths.get(RouteFileName)).
                collect(Collectors.toList());

        Route r = ProcessRouteName(lineas.get(0));

        long errores=lineas.stream().skip(2).
                map(linea -> process_route_assignment(linea,r)).
                filter(flag -> !flag).count();

        return (int) errores;

    }



    private Map<Route, Long> auxiliary_countInRoutes(Set<String> sector){
        // represents the result ...
        Map<Route,Long> result = new HashMap<>();

        // auxiliary variables, used to count the IDs in each route ...
        long route1_ = 0;
        long route2_ = 0;
        long route3_ = 0;
        long no_route = 0;

        // for each sector ...
        for(String ID: sector){

            /*
             * The following conditional statements, are all if statments, because maybe an ID can be repeated
             * in more than 1 ROUTE.
             * */
            if(this.route1.contains(ID)){
                route1_++;
            }
            if(this.route2.contains(ID)){
                route2_++;
            }
            if(this.route3.contains(ID)){
                route3_++;
            }
            if(this.no_route.contains(ID)){
                no_route++;
            }
        }

        // storing the results ...
        result.put(Route.RUTA1,route1_);
        result.put(Route.RUTA2,route2_);
        result.put(Route.RUTA3,route3_);
        result.put(Route.NORUTA,no_route);


        return result;

    }

    public Map<Route, Long> getCounterRoute(Sector sector_name){

        // represents the result ...
        Map<Route,Long> result = new HashMap<>();

        switch (sector_name){
            case SECTOR1: //In case of SECTOR1, we work with the class data member sector1
                result = this.auxiliary_countInRoutes(this.sector1);
                break;
            case SECTOR2:
                result = this.auxiliary_countInRoutes(this.sector2);
                break;
            case NOSECTOR:
                result = this.auxiliary_countInRoutes(this.no_sector);

        }

        return result;
    }

    private void CountNoSectorEmployees(){
        no_sector = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() == Sector.NOSECTOR)
                .map(Employee::obtenerDni)
                .collect(Collectors.toSet());
    }

    private void CountNoRouteEmployees(){

        no_route = employee_map.values().stream()
                .filter(emp -> emp.obtenerRuta() == Route.NORUTA)
                .map(Employee::obtenerDni)
                .collect(Collectors.toSet());
    }

    public void auxiliary_validate(){
        this.CountNoRouteEmployees();
        this.CountNoSectorEmployees();
    }
    /**
     * getCounterSectorRoute, is a function, used to get the distribution of the employees in the sections
     * */
    public Map<Sector, Map<Route, Long>> getCounterSectorRoute() throws InterruptedException {
        // result ...
        Map<Sector, Map<Route, Long>> result = new HashMap<>();

        // Auxiliary array, used to not repeat code, when calling getCounterRoute(s) for each secotr
        ArrayList<Sector> sectors = new ArrayList<>();

        // Adding the sectors ...
        sectors.add(Sector.SECTOR1);
        sectors.add(Sector.SECTOR2);
        sectors.add(Sector.NOSECTOR);


        // Making the execution parallel ...
        Thread thread1 = new Thread(() -> result.put(Sector.SECTOR1,getCounterRoute(Sector.SECTOR1)));
        Thread thread2 = new Thread(() -> result.put(Sector.SECTOR2,getCounterRoute(Sector.SECTOR2)));
        Thread thread3 = new Thread(() -> result.put(Sector.NOSECTOR,getCounterRoute(Sector.NOSECTOR)));

        // Starting the execution ...
        thread1.start();
        thread2.start();
        thread3.start();

        // Waiting for them to finish ...
        thread1.join();
        thread2.join();
        thread3.join();


        return result;
    }

    //public Map<srcPackage.Sector,Long> getCounterSector(){ }

    public List<Employee> searchEmployeesWithoutSectorNorRoute(){
        List<Employee> res = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() == Sector.NOSECTOR &&
                               emp.obtenerRuta() == Route.NORUTA
                        )
                .collect(Collectors.toList());

        return res;
    }

    public List<Employee> searchEmployeesWithoutRoute(Sector s){
        List<Employee> res = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() == s &&
                               emp.obtenerRuta() == Route.NORUTA)
                .collect(Collectors.toList());

        return res;
    }

    public List<Employee> searchEmployeesWithSectorWithoutRoute(){
        List<Employee> res = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() != Sector.NOSECTOR &&
                               emp.obtenerRuta() == Route.NORUTA
                       )
                .collect(Collectors.toList());

        return res;
    }

    public List<Employee> searchEmployeesWithoutSector(Route r){
        List<Employee> res = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() == Sector.NOSECTOR &&
                               emp.obtenerRuta() == r
                       )
                .collect(Collectors.toList());

        return res;
    }

    public List<Employee> searchEmployeesWithoutSectorWithRoute(){
        List<Employee> res = employee_map.values().stream()
                .filter(emp -> emp.obtenerSector() == Sector.NOSECTOR &&
                               emp.obtenerRuta() != Route.NORUTA
                )
                .collect(Collectors.toList());

        return res;
    }

}
