public class Resource {

    char name;



    int volume;

    public Resource(char name, int volume){
        this.name=name;
        this.volume=volume;
    }

    public char getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String toString(){
       return name+":"+volume;
    }




}
