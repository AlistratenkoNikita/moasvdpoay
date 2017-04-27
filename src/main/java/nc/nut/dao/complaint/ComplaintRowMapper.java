package nc.nut.dao.complaint;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Revniuk Aleksandr
 */
@Component
public class ComplaintRowMapper implements RowMapper<Complaint> {
    @Override
    public Complaint mapRow(ResultSet resultSet, int i) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setId(resultSet.getInt("ID"));
        complaint.setOrder_id(resultSet.getInt("ORDER_ID"));
        complaint.setCsr_id(resultSet.getInt("CSR_ID"));
        Calendar calendar = new GregorianCalendar();
        resultSet.getDate("CREATING_DATE",calendar);
        complaint.setCreation_date(calendar);
        complaint.setStatus_id(resultSet.getInt("STATUS_ID"));
        complaint.setDescription(resultSet.getString("DESCRIPTION"));
        return complaint;
    }
}
