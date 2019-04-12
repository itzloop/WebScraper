package sample;

import java.util.ArrayList;

public class Trailers extends ArrayList<Trailer> {
    private String name;


    Trailers(String name)
    {this.name = name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void LoadTrailer(String data)
    {

        String[] datas  = data.split("\"videoMature\":");
        for (int i = 0; i < count(data); i++) {
                String videoId = datas[i].split("\"videoId\":\"")[1].split("\",")[0];
                String description = datas[i].split("\"description\":\"")[1].split("\",")[0];
                String duration = datas[i].split("\"duration\":\"")[1].split("\",")[0];
                ArrayList<Encoding> encodings = loadEncodings(datas[i].split("\"encodings\":\\[")[1]);
                this.add(new Trailer(name,videoId,description,duration,encodings));
        }

    }

    private ArrayList<Encoding> loadEncodings(String encodingsText)
    {
        ArrayList<Encoding> tempEncodings = new ArrayList<>();
        String[] encodingtTemp = encodingsText.split("},");
        int count= encodingsText.split("],").length;
        for (int i = 0; i <count; i++) {
            tempEncodings.add(new Encoding(encodingtTemp[i].split("\"definition\":\"")[1].split("\",")[0] ,
                    encodingtTemp[i].split("\"videoUrl\":\"")[1].split("\"}")[0].replace("\\u002F" , "/")));
//            tempEncodings.add(new Encoding(encodingsText.split("\"definition\":\"")[1].split("\",")[0],
//                    encodingsText.split("\"videoUrl\":\"")[1].split("\"}")[0]
//                    ));
        }

        return tempEncodings;
    }


    private int count(String data)
    {
        int count = data.split("\"videoMature\":").length - 1;
        return count;
    }



}
