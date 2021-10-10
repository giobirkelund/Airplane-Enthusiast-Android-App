package com.example.flyapp.models

fun initMyAirports() : MutableList<Airport> {
    val gard = Airport("ENGM", "Gardermoen, Oslo Airport", 60.19, 11.1,27482486, "https://images.adsttc.com/media/images/5916/72d9/e58e/cea8/b300/00a7/large_jpg/Photography_Ivan_Brodey10.jpg?1494643351")
    val fls = Airport("ENBR", "Bergen, Flesland", 60.29, 5.21,6113452, "https://static.routesonline.com/images/cached/newsarticle-274360-scaled-580x0.jpg")
    val trnd = Airport("ENVA", "Trondheim, Værnes", 63.45, 10.92,4428897, "https://pcflight.net/wp-content/uploads/2017/04/ENVA_023.jpg.bb0a2d535d7b8879461b601d3502d238.jpg")
    val stav = Airport("ENZV", "Stavanger, Sola", 58.87, 5.63,4178241, "https://airmundo.com/app/uploads/2017/07/StavangerAirport-400x266.jpg")
    val tromso = Airport("ENTC", "Tromsø, Tromsø Airport", 69.68,18.91,2271748, "https://norwaytoday.info/wp-content/uploads/2020/04/NTB_eC7GBZW8L1k.jpg")
    val sande = Airport("ENTO", "Sandefjord, Torp", 59.18,10.25,1965558, "https://media-exp1.licdn.com/dms/image/C4E1BAQHCWGmUIMjqnA/company-background_10000/0?e=2159024400&v=beta&t=jzsu4iZpbq8cI06LWIHYmvM5PwZPEGfNGlKEsl6Ulp8")
    val bodo = Airport("ENBO", "Bodø, Bodø airport", 67.26,14.36,1831407, "https://moodie-website-archive.s3.eu-west-2.amazonaws.com/Bodo_Lufthavn_600.jpg")
    val alesund = Airport("ENAL", "Ålesund, Vigra",62.56,6.11,1077009, "https://berloga-workshop.com/cache/cache_image/f/f1aa9a99a2484b94767dd8436595d1dc.png")
    val krist = Airport("ENCN", "Kristiansand, Kjevik", 58.20,8.08,1031048, "https://upload.wikimedia.org/wikipedia/commons/8/84/KRS_Approach_RWY04.jpg")
    val narvik = Airport("ENEV", "Narvik, Evenes", 68.48,16.67, 755442, "https://upload.wikimedia.org/wikipedia/commons/1/11/Harstad%2C_Troms_fylke%2C_Nord_Norge_%2812267584645%29.jpg")
    val haugesund = Airport("ENHD", "Haugesund, Karmøy", 59.34,5.21,633712, "https://haugalandvekst.no/app/uploads/2019/05/Lufthavn.jpg")
    val molde = Airport("ENML", "Molde, Årø", 62.74,7.26,478475, "http://www.erdetmulig.no/images/moldeflyplass2.jpg")
    val alta = Airport("ENAT", "Alta, Alta Airport", 69.97,23.37,378891, "https://g.acdn.no/obscura/API/dynamic/r1/ece5/tr_1000_2000_s_f/0000/ifin/2018/3/14/9/Alta%2BLufthavn%2B2%2B-%2BKopi.jpg?chk=522EAF")
    val kirkenes = Airport("ENKR", "Kirkenes, Høybuktmoen", 69.72,29.88,318194, "https://upload.wikimedia.org/wikipedia/commons/e/e4/Kirkenes_Airport_20081006.jpg")
    val kvern = Airport("ENKB", "Kristiansund, Kvernberget", 63.11,7.82,299710, "https://upload.wikimedia.org/wikipedia/commons/d/d1/Kristiansund_Airport.png")
    val bardufoss = Airport("ENDU", "Bardufoss, Bardufoss Airport", 69.05,18.54,243104, "https://robinlund.no/wp-content/uploads/2017/02/p1190225-CopyrightRobinLund-bardufoss-lufthavn.jpg")
    val hammer = Airport("ENHF", "Hammerfest, Hammerfest Airport",70.67,23.66,194287, "https://upload.wikimedia.org/wikipedia/commons/b/bb/Hammerfest_Airport.jpg")
    val lakselv = Airport("ENNA", "Lakselv, Banak", 70.06,24.96,65628,"https://media.snl.no/media/173043/standard_Banak.jpg")
    val moss = Airport("ENRY","Moss, Rygge",59.37,10.78,1642156,"https://www.forsvarsbygg.no/contentassets/60eca782abca4f15b0a0d178828f26d9/11183_1.jpg")
    val floro = Airport("ENFL","Florø, Florø Airport",61.58,5.02,176795,"https://g.acdn.no/obscura/API/dynamic/r1/ece5/tr_480_360_l_f/0000/fipo/2019/6/17/8/flyplassen5ny.jpg?chk=113EC3")
    val bronnoysund = Airport("ENBN","Brønnøy, Brønnesund airport",65.46,12.21,129600,"https://upload.wikimedia.org/wikipedia/commons/7/7a/Br%C3%B8nn%C3%B8ysund_lufthavn.jpg")
    val orsta = Airport("ENOV","Ørta/Volden, Hovden",62.18,6.08,115009,"https://www.morenytt.no/frapapir/article13085012.ece/7y2wh4/ALTERNATES/w980-default/dsc_0121.jpg")
    val moIRana = Airport("ENRA","Mo i Rana, Røssvoll",66.36,14.3,119641,"https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Mo_I_Rana_Airport.JPG/250px-Mo_I_Rana_Airport.JPG")
    val leksnes = Airport("ENLK","Leksnes, Leksnes Airport",68.15,13.61,105470,"https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/Leknes_lufthavn.jpg/250px-Leknes_lufthavn.jpg")
    val skagen = Airport("ENSK","Skagen, Stokmarknes Airport",68.58,15.03,107400,"https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Stokmarknes_Airport%2C_Skagen_%282015%29.jpg/250px-Stokmarknes_Airport%2C_Skagen_%282015%29.jpg")

    return mutableListOf(gard, fls, trnd, stav,tromso,sande,bodo,alesund,krist, narvik,
            haugesund,molde, alta, kirkenes,kvern,bardufoss,hammer,lakselv,moss,floro,bronnoysund,orsta,moIRana,leksnes,skagen)
}