package cz.vse.pexeso.common.message.payload;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RevealCardPayload implements MessagePayload {

    public int row;
    public int column;

    public RevealCardPayload() {
    }

    public RevealCardPayload(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public RevealCardPayload(String data) {
        var mapper = new ObjectMapper();

        try {
            RevealCardPayload parsed = mapper.readValue(data, RevealCardPayload.class);

            this.row = parsed.row;
            this.column = parsed.column;
        } catch(Exception e) {}
    }

	@Override
	public String toSendable() {
        var mapper = new ObjectMapper();

        String ret = "";

        try {
            ret = mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
	}

}
