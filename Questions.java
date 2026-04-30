import java.util.ArrayList;

// ════════════════════════════════════════════════════════════════════════
//  Questions.java
//  All game questions separated by difficulty.
//  Used by GameScreen to load Easy, Medium, or Hard question sets.
// ════════════════════════════════════════════════════════════════════════
public class Questions {

    public ArrayList<Question> easyQuestions   = new ArrayList<>();
    public ArrayList<Question> mediumQuestions = new ArrayList<>();
    public ArrayList<Question> hardQuestions   = new ArrayList<>();

    public Questions() {
        loadEasy();
        loadMedium();
        loadHard();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  EASY – Luzon cities
    // ══════════════════════════════════════════════════════════════════════
    private void loadEasy() {
        easyQuestions.add(new Question("What is the capital city of the Philippines?","AMANIL",new String[]{"Manila","Baguio","Malolos","Olongapo"},"Manila"));
        easyQuestions.add(new Question("What is the largest city in Metro Manila?","NOZEQU ITYC",new String[]{"Makati","Imus","Angeles","Quezon City"},"Quezon City"));
        easyQuestions.add(new Question("Which city is known as the financial center of Metro Manila?","ATKIMA",new String[]{"Malolos","Makati","Cabanatuan","San Jose Del Monte"},"Makati"));
        easyQuestions.add(new Question("Which city is known for the Baguio Strawberry Farm nearby?","GBAUIO",new String[]{"Lucena","San Fernando","Baguio","Antipolo"},"Baguio"));
        easyQuestions.add(new Question("What city is the capital of Pampanga?","NAS DONREFNA",new String[]{"Makati","San Fernando","Olongapo","Quezon"},"San Fernando"));
        easyQuestions.add(new Question("Which city in Pampanga is famous for nightlife and Clark Freeport Zone?","SLAEGNE ITYC",new String[]{"Malolos","San Fernando","Antipolo","Angeles City"},"Angeles City"));
        easyQuestions.add(new Question("What city is the capital of Bulacan?","AMOSOLL",new String[]{"Malolos","Marikina","Batangas","San Pablo"},"Malolos"));
        easyQuestions.add(new Question("What city in Bulacan is one of the most populated in the province?","ANS SOJE LED TOMNE",new String[]{"Iriga","Naga","San Jose Del Monte","San Pablo"},"San Jose Del Monte"));
        easyQuestions.add(new Question("What city in Zambales is near Subic Bay?","POONGOAL",new String[]{"Antipolo","Olongapo","Tagaytay","San Jose Del Monte"},"Olongapo"));
        easyQuestions.add(new Question("What city is the capital of Nueva Ecija?","UABAATNNCA",new String[]{"Cabanatuan","Malolos","Calamba","Lipa"},"Cabanatuan"));
        easyQuestions.add(new Question("What city in Rizal is known for the Antipolo Cathedral?","LOOPTIAN",new String[]{"Batangas","Calamba","Antipolo","Santa Rosa"},"Antipolo"));
        easyQuestions.add(new Question("Which city in Cavite is known for the Imus Cathedral?","IUMS",new String[]{"Imus","Calamba","Lipa","Cabanatuan"},"Imus"));
        easyQuestions.add(new Question("What Cavite city is famous for Tagaytay Ridge views?","YATAGYAT",new String[]{"Tagaytay","Lucena","Lipa","Legazpi"},"Tagaytay"));
        easyQuestions.add(new Question("Which city in Laguna is the hometown of Jose Rizal?","MACALBA",new String[]{"Lipa","Legazpi","Calamba","Marikina"},"Calamba"));
        easyQuestions.add(new Question("Which Laguna city is known for its Seven Lakes?","ASN LPABO",new String[]{"San Jose Del Monte","San Pablo","San Fernando","Santa Rosa"},"San Pablo"));
        easyQuestions.add(new Question("What city in Laguna is a major industrial hub?","SORA TAANS",new String[]{"Tabaco","Dagupan","San Fernando","Santa Rosa"},"Santa Rosa"));
        easyQuestions.add(new Question("What city is the capital of Batangas?","SAAGANTB TICY",new String[]{"Quezon City","Batangas City","Sorsogon City","Masbate City"},"Batangas City"));
        easyQuestions.add(new Question("Which Batangas city is famous for coffee?","IAPL",new String[]{"Lipa","Lucena","Ligao","Naga"},"Lipa"));
        easyQuestions.add(new Question("What city is the capital of Quezon Province?","NAUECL",new String[]{"Vigan","Laoag","Lucena","Naga"},"Lucena"));
        easyQuestions.add(new Question("Which city is known as the gateway to Mayon Volcano?","ZAGELIP",new String[]{"Laoag","Olongapo","Lucena","Legazpi"},"Legazpi"));
        easyQuestions.add(new Question("What city in Albay is known for Tabaco knives?","BAAOTC",new String[]{"Naga","Tabaco","Masbate City","Ligao"},"Tabaco"));
        easyQuestions.add(new Question("Which city in Albay is called the vegetable bowl of Bicol?","IAOGL",new String[]{"Ligao","Lipa","Tabaco","Naga"},"Ligao"));
        easyQuestions.add(new Question("What city is known as the Heart of Bicol?","AANG",new String[]{"Sorsogon City","Tabaco","Naga","Iriga"},"Naga"));
        easyQuestions.add(new Question("Which city in Camarines Sur is famous for Mount Asog?","IRGAI",new String[]{"Sorsogon City","Tabaco","Naga","Iriga"},"Iriga"));
        easyQuestions.add(new Question("What city is the capital of Masbate?","TICY ABTESAM",new String[]{"Sorsogon City","San Jose City","Masbate City","Batangas City"},"Masbate City"));
        easyQuestions.add(new Question("What city is the capital of Sorsogon Province?","OOSSROGN YITC",new String[]{"Angeles City","Sorsogon City","Quezon City","Batangas City"},"Sorsogon City"));
        easyQuestions.add(new Question("What city in Pangasinan is famous for Bangus Festival?","PUGANAD",new String[]{"Vigan","Cauayan","Santiago","Dagupan"},"Dagupan"));
        easyQuestions.add(new Question("What city in Ilocos Norte is the provincial capital?","GAOAL",new String[]{"San Jose City","Laoag","Meycauayan","Marikina"},"Laoag"));
        easyQuestions.add(new Question("What historic city in Ilocos Sur is known for Spanish architecture?","GAIVN",new String[]{"Meycauyan","Santiago","Vigan","Marikina"},"Vigan"));
        easyQuestions.add(new Question("What city is the capital of Cagayan Province?","UGGEUARTAO",new String[]{"Meycauyan","Tuguegarao","Laoag","Cauayan"},"Tuguegarao"));
        easyQuestions.add(new Question("Which city in Isabela is known for rice production?","YAANUCA",new String[]{"Cauayan","San Juan","Pasig","Olongapo"},"Cauayan"));
        easyQuestions.add(new Question("Which Isabela city is known as the Queen City of the North?","GOATINAS",new String[]{"Santiago","San Juan","Pasig","Olongapo"},"Santiago"));
        easyQuestions.add(new Question("What city in Nueva Ecija is called the rice granary city?","OSEJ NAS CYTI",new String[]{"Masbate City","Sorsogon City","Quezon City","San Jose City"},"San Jose City"));
        easyQuestions.add(new Question("What city in Bulacan is known for shoe factories nearby?","YUMYANEACA",new String[]{"Santa Rosa","Meycauayan","Malolos","Lucena"},"Meycauayan"));
        easyQuestions.add(new Question("Which Metro Manila city is known for shoe industry?","KIMRAINA",new String[]{"Marikina","Pasig","Caloocan","Lucena"},"Marikina"));
        easyQuestions.add(new Question("Which Metro Manila city hosts Ninoy Aquino International Airport?","AYAPS",new String[]{"Makati","San Juan","Caloocan","Pasay"},"Pasay"));
        easyQuestions.add(new Question("What Metro Manila city contains Bonifacio Global City?","GAGIUT",new String[]{"Cauayan","Marikina","Pasig","Taguig"},"Taguig"));
        easyQuestions.add(new Question("Which Metro Manila city is famous for Ortigas Center?","ISGPA",new String[]{"Pasay","Caloocan","Pasig","Santa Rosa"},"Pasig"));
        easyQuestions.add(new Question("What Metro Manila city is known for Greenhills Shopping Center?","UNJA ASN",new String[]{"Santiago","San Jose Del Monte","San Juan","Santa Rosa"},"San Juan"));
        easyQuestions.add(new Question("Which Metro Manila city is the northern gateway of NCR?","AALCOCON",new String[]{"Calamba","Cabanatuan","Manila","Caloocan"},"Caloocan"));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MEDIUM – Visayas cities 
    // ══════════════════════════════════════════════════════════════════════
    private void loadMedium() {
        mediumQuestions.add(new Question("What is the Queen City of the South?","CCBUTIEY",new String[]{"Quezon City","Cebu City","Iloilo City","Roxas City"},"Cebu City"));
        mediumQuestions.add(new Question("Which city is next to Cebu City, known for its furniture industry?","AEMANDU",new String[]{"Dumaguete","Lapu-Lapu","Mandaue","Bacolod"},"Mandaue"));
        mediumQuestions.add(new Question("Which city is on Mactan Island, known for Magellan's Cross?","LPAUUPLA",new String[]{"Tagbilaran","Ormoc","Lapu-Lapu","Tacloban"},"Lapu-Lapu"));
        mediumQuestions.add(new Question("Which city is famous as the City of Gentle People?","ETUGDAMUE",new String[]{"Dumaguete","Baybay","Carcar","Mandaue"},"Dumaguete"));
        mediumQuestions.add(new Question("What city is called the City of Smiles?","AOLCODB",new String[]{"Baybay","Carcar","Bacolod","Bais"},"Bacolod"));
        mediumQuestions.add(new Question("Which city is known for the Dinagyang Festival?","OLIILO YTIC",new String[]{"Iloilo City","Roxas City","Sorsogon City","San Jose City"},"Iloilo City"));
        mediumQuestions.add(new Question("Which city is the seafood capital of the Philippines?","SAXRO ITYC",new String[]{"Angeles City","Batangas City","Bacolod","Roxas City"},"Roxas City"));
        mediumQuestions.add(new Question("Which city is the capital of Leyte and famous for the Santo Niño celebration?","OATNCLAB",new String[]{"Tagaytay","Talisay","Tacloban","Tagbilaran"},"Tacloban"));
        mediumQuestions.add(new Question("Which Leyte city is known for its waterfalls and rich agriculture?","COROM",new String[]{"Ormoc","Pasay","Lapu-Lapu","Taguig"},"Ormoc"));
        mediumQuestions.add(new Question("What is the capital city of Bohol?","RANABTIGAL",new String[]{"Tagaytay","Tuguegarao","Tabaco","Tagbilaran"},"Tagbilaran"));
        mediumQuestions.add(new Question("Which city is just south of Cebu City?","SYAATIL",new String[]{"Lucena","Tabaco","Tuguegarao","Talisay"},"Talisay"));
        mediumQuestions.add(new Question("Which city is famous for heritage houses and lechon?","ARCCAR",new String[]{"Cadiz","Carcar","Calamba","Carles"},"Carcar"));
        mediumQuestions.add(new Question("Which Leyte city is known for its beaches?","BYAAYB",new String[]{"Bogo","Bayawan","Baybay","Bais"},"Baybay"));
        mediumQuestions.add(new Question("Which northern Cebu city is a gateway to the northern towns?","OGBO",new String[]{"Baybay","Baguio","Bacolod","Bogo"},"Bogo"));
        mediumQuestions.add(new Question("Which Negros Oriental city is known for sugar production?","ULNGUHAING",new String[]{"Guihulngan","Dumaguete","Bais","Canlaon"},"Guihulngan"));
        mediumQuestions.add(new Question("Which Negros Occidental city is famous for its seafood and sugar?","ACDIZ",new String[]{"Talisay","Cadiz","Silay","Canlaon"},"Cadiz"));
        mediumQuestions.add(new Question("Which city is called the 'Paris of Negros' for its heritage houses?","YAILS",new String[]{"Talisay","Silay","Canlaon","Cadiz"},"Silay"));
        mediumQuestions.add(new Question("Which Negros Oriental city is near Dumaguete?","AJTANY",new String[]{"Dumaguete","Bais","Bayawan","Tanjay"},"Tanjay"));
        mediumQuestions.add(new Question("Which Iloilo city is known for sugar industry and sugar cane farms?","PISAS",new String[]{"Dumangas","Iloilo City","Pototan","Passi"},"Passi"));
        mediumQuestions.add(new Question("Which Iloilo city is a coastal city famous for islands and beaches?","ARLESC",new String[]{"Passi","Iloilo City","Carles","Dumangas"},"Carles"));
        mediumQuestions.add(new Question("Which Leyte city is near Lake Danao and frequently hit by typhoons?","MOROC",new String[]{"Tacloban","Baybay","Ormoc","Maasin"},"Ormoc"));
        mediumQuestions.add(new Question("Which Negros Oriental city is known for agriculture?","NYABAWA",new String[]{"Escalante","Bayawan","Danao","Tanjay"},"Bayawan"));
        mediumQuestions.add(new Question("Which Negros Oriental city is famous for sea turtles?","SBIA",new String[]{"Himamaylan","La Corta","Toledo","Bais"},"Bais"));
        mediumQuestions.add(new Question("Which city hosts Silliman University?","MEUTEAUDG",new String[]{"Dumaguete","Bogo","Mabinay","Victorias"},"Dumaguete"));
        mediumQuestions.add(new Question("Which city is a gateway to northern Negros Occidental?","ACRSLANOS",new String[]{"San Jose Del Monte","San Jose","San Juan","San Carlos"},"San Carlos"));
        mediumQuestions.add(new Question("Which Negros Occidental city is on the southern coast?","LAMYHINAMA",new String[]{"Kabankalan","Sipalay","Himamaylan","Binalbagan"},"Himamaylan"));
        mediumQuestions.add(new Question("Which city is known for its sugar industry and port?","TNEAELCSA",new String[]{"Bago","Escalante","Cadiz","Guihulngan"},"Escalante"));
        mediumQuestions.add(new Question("Which Negros Occidental city is known for its agricultural festivals?","ALACROLTA",new String[]{"Victorias","La Carlota","Toledo","Sibulan"},"La Carlota"));
        mediumQuestions.add(new Question("Which city is a port city in Negros Oriental?","IBAS",new String[]{"Carcar","Mabinay","Toledo","Bais"},"Bais"));
        mediumQuestions.add(new Question("Which Negros Oriental town is known for caves?","BAYMINA",new String[]{"Carcar","Mabinay","Toledo","Bais"},"Mabinay"));
        mediumQuestions.add(new Question("Which northern Cebu city hosts festivals like the Pilgrimage of St. John the Baptist?","OOGB",new String[]{"La Carlota","Victorias","Escalante","Bogo"},"Bogo"));
        mediumQuestions.add(new Question("Which city is known as the Gateway to Northern Negros Oriental?","HUGANGLIUN",new String[]{"Silay","Guihulngan","Talisay","Victorias"},"Guihulngan"));
        mediumQuestions.add(new Question("Which city is known for heritage architecture and chicharon?","CRACAR",new String[]{"Mandaue","Cebu City","Carcar","Lapu-Lapu"},"Carcar"));
        mediumQuestions.add(new Question("Which city is famous for its sweet delicacies?","AISATLY",new String[]{"Tagaytay","Taguig","Toledo","Talisay"},"Talisay"));
        mediumQuestions.add(new Question("Which city is home to Victorias Milling Company?","AIVRTCOSI",new String[]{"Victorias","Bogo","Silay","Talisay"},"Victorias"));
        mediumQuestions.add(new Question("Which city is considered the cultural capital of Negros Occidental?","AILSY",new String[]{"Silay","Carcar","Bais","Cadiz"},"Silay"));
        mediumQuestions.add(new Question("Which Negros Oriental town has Dumaguete Airport?","NUBLISA",new String[]{"Mabinay","Sibulan","Bogo","Toledo"},"Sibulan"));
        mediumQuestions.add(new Question("Which Cebu city is famous for furniture and its hanging bridge?","AAODN",new String[]{"Escalante","Davao","La Corta","Danao"},"Danao"));
        mediumQuestions.add(new Question("Which city is known as the Sugar Bowl of Negros?","AICZD",new String[]{"Silay","Caloocan","Cadiz","Bacolod"},"Cadiz"));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HARD – Mindanao cities
    // ══════════════════════════════════════════════════════════════════════
    private void loadHard() {
        hardQuestions.add(new Question("Which city is known as the 'City of Golden Friendship'?","AUBNUT",new String[]{"Davao","Cagayan de Oro","Tagum","Butuan"},"Butuan"));
        hardQuestions.add(new Question("Which city is the economic center of Northern Mindanao?","AAYCNAG ED OOR",new String[]{"Ipil","Davao","Cagayan de Oro","Lamitan"},"Cagayan de Oro"));
        hardQuestions.add(new Question("Which city is famous for Tuna Festival?","GENERLA TNOSAS",new String[]{"Prosperidad","General Santos","Bayugan","Isabela"},"General Santos"));
        hardQuestions.add(new Question("Which city is nicknamed 'City of Flowers'?","MLAALBYAAY",new String[]{"Davao","Tagum","Malaybalay","Tandag"},"Malaybalay"));
        hardQuestions.add(new Question("Which city is a gateway to Samal Island?","VDOAA",new String[]{"Zamboanga","Davao","Cagayan de Oro","Iligan"},"Davao"));
        hardQuestions.add(new Question("Which city is known for the Linabo Hills and Bukidnon Museum?","MYAYBAALLA",new String[]{"Butuan","Tagum","Malaybalay","Davao"},"Malaybalay"));
        hardQuestions.add(new Question("Which city hosts the Kadayawan Festival?","AOADV",new String[]{"General Santos","Cagayan de Oro","Iligan","Davao"},"Davao"));
        hardQuestions.add(new Question("Which city is the capital of Zamboanga del Norte?","GODIPOL",new String[]{"Butuan","Iligan","Dipolog","Cagayan de Oro"},"Dipolog"));
        hardQuestions.add(new Question("Which city is known as the 'Industrial Capital of Mindanao'?","AVODA",new String[]{"Tagum","Cagayan de Oro","Davao","Butuan"},"Davao"));
        hardQuestions.add(new Question("Which city is famous for its sardines and fishing industry?","NLIAGI",new String[]{"Dapitan","Iligan","Butuan","Dipolog"},"Iligan"));
        hardQuestions.add(new Question("What city is the capital of Surigao del Norte?","SOGRAIU",new String[]{"Tagum","Surigao","Butuan","General Santos"},"Surigao"));
        hardQuestions.add(new Question("What Bukidnon city is known for pineapple plantations?","AECVINAL",new String[]{"Iligan","Ozamiz","Surigao","Valencia"},"Valencia"));
        hardQuestions.add(new Question("Which city is a hub for Mindanao's business and banking?","AYGAANC ED OOR",new String[]{"Butan","Davao","Tandag","Cagayan de Oro"},"Cagayan de Oro"));
        hardQuestions.add(new Question("Which city is known for Mt. Apo, the highest peak in the Philippines?","DAOAV",new String[]{"Panabo","Bislig","Davao","Tubod"},"Davao"));
        hardQuestions.add(new Question("Which city is famous for Halad Festival?","AGTMU",new String[]{"Tacurong","Kidapawan","Dapitan","Tagum"},"Tagum"));
        hardQuestions.add(new Question("Which city is known for its historic Fort Pilar?","NABMAZAOG",new String[]{"General Santos","Zamboanga","Koronadal","Cotabato"},"Zamboanga"));
        hardQuestions.add(new Question("What city in Davao del Norte is known for banana plantations?","AOBANP",new String[]{"Samal","Mati","Panabo","Digos"},"Panabo"));
        hardQuestions.add(new Question("Which city is called 'City of Flowers and Pineapples'?","MYAYBAALLA",new String[]{"Malaybalay","Tagum","Davao","Iligan"},"Malaybalay"));
        hardQuestions.add(new Question("Which city is famous for its historical heritage and Balangay boats?","UUANBT",new String[]{"Cagayan De Oro","Butuan","Iligan","Tagum"},"Butuan"));
        hardQuestions.add(new Question("Which city is famous for Abaca and Banana plantations?","UGMTA",new String[]{"Malaybalay","General Santos","Tagum","Iligan"},"Tagum"));
        hardQuestions.add(new Question("Which city is the southernmost major city in Mindanao?","ALREENG TSOANS",new String[]{"Butuan","Davao","Dapitan","General Santos"},"General Santos"));
        hardQuestions.add(new Question("Which city is known as the 'City of Roses'?","LYBMALAAAY",new String[]{"Malaybalay","Tagum","Butuan","Davao"},"Malaybalay"));
        hardQuestions.add(new Question("Which city hosts the Hermosa Festival?","PADINAT",new String[]{"Dapitan","Lamitan","Mambajao","El Salvador"},"Dapitan"));
        hardQuestions.add(new Question("Which city is known for its tuna industry and sports facilities?","LRENAGE NOTSAS",new String[]{"General Santos","Davao","Cagayan de Oro","Tagum"},"General Santos"));
        hardQuestions.add(new Question("Which city is nicknamed 'City of Golden Friendship' due to trade and commerce?","UUATNB",new String[]{"Isabela","Marawi","Butuan","Oroquieta"},"Butuan"));
        hardQuestions.add(new Question("Which city is the capital of Lanao del Norte?","ILAGNI",new String[]{"Bayugan","Tangub","Tubod","Iligan"},"Iligan"));
        hardQuestions.add(new Question("Which city is famous for the Mindanao Arts Festival?","AADOV",new String[]{"Ozamiz","Marawi","Prosperidad","Davao"},"Davao"));
        hardQuestions.add(new Question("Which city is a key hub for Mindanao's trade and commerce?","AYGAANC ED OOR",new String[]{"El Salvador","Mambajao","Cagayan de Oro","Malaybalay"},"Cagayan de Oro"));
        hardQuestions.add(new Question("Which city hosts the Tuna Capital of the Philippines?","LARENGE SONTAS",new String[]{"Davao","General Santos","Ipil","Tangub"},"General Santos"));
        hardQuestions.add(new Question("Which city is known for Balangay heritage and old churches?","UAUTBN",new String[]{"Davao","Pagadian","Butuan","Kidapawan"},"Butuan"));
        hardQuestions.add(new Question("Which city is famous for whitewater activities and adventure tourism?","AYGAANC ED OOR",new String[]{"Surigao","Bislig","Cagayan de Oro","Panabo"},"Cagayan de Oro"));
        hardQuestions.add(new Question("Which city is home to Mt. Apo and the Davao Crocodile Park?","VAOAD",new String[]{"Davao","Tagum","Iligan","General Santos"},"Davao"));
        hardQuestions.add(new Question("Which city in Mindanao celebrates the Halad Festival?","TAGUM",new String[]{"Tagum","Davao","Malaybalay","Cagayan de Oro"},"Tagum"));
        hardQuestions.add(new Question("Which city is known for its historical Fort Pilar and Chavacano culture?","NABMAZAOG",new String[]{"Zamboanga","Davao","Iligan","Dipolog"},"Zamboanga"));
        hardQuestions.add(new Question("Which city is the economic hub of Northern Mindanao?","GAAAYCN ED OOR",new String[]{"Cagayan de Oro","Davao","Iligan","Tagum"},"Cagayan de Oro"));
        hardQuestions.add(new Question("Which city is famous for pineapple and flower plantations in Bukidnon?","MYAYBAALLA",new String[]{"Malaybalay","Tagum","Butuan","Davao"},"Malaybalay"));
        hardQuestions.add(new Question("Which city is the southernmost tip of Mindanao and famous for sports?","LRENAGE NOTSAS",new String[]{"Panabo","General Santos","Cagayan de Oro","Dipolog"},"General Santos"));
        hardQuestions.add(new Question("Which city is called the 'City of Friendship'?","UAUTNB",new String[]{"Bislig","Pagadian","Ozamiz","Butuan"},"Butuan"));
        hardQuestions.add(new Question("Which city is known for Maria Cristina Falls?","GIINLA",new String[]{"Davao","Butuan","Iligan","Tagum"},"Iligan"));
        hardQuestions.add(new Question("Which city is known as the gateway to Northern Mindanao?","GAIILN",new String[]{"Cagayan de Oro","Davao","Dipolog","Iligan"},"Iligan"));
    }
}