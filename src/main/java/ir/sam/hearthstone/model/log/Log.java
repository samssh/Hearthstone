package ir.sam.hearthstone.model.log;

import ir.sam.hearthstone.hibernate.SaveAble;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@EqualsAndHashCode(of = "time")
public abstract class Log implements SaveAble {
    @Id
    @Getter
    @Setter
    protected long time;
    @Getter
    @Setter
    private String username;

    public Log(long time, String username) {
        this.time = time;
        this.username = username;
    }

    public Log() {
    }
}
