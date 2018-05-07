package GUI.Network;

import Common.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Date;
import java.time.LocalDate;

public class ExcursionData {
	public static ExcursionListResponse GetExcursions() throws IOException, ClassNotFoundException {
		SocketInstance sock = SocketInstance.getInstance();
		
		sock.Send(new Request(Code.GET_EXCURSIONS).GetSerialized());
		
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		ExcursionListResponse responseList = (ExcursionListResponse) os.readObject();
		
		os.close();
		sock.Close();
		
		return responseList;
	}
	
	public static Code BookExcursion(int userId, int excursionId, LocalDate date, int passengerCount) throws IOException, ClassNotFoundException {
		SocketInstance sock = SocketInstance.getInstance();
		
		Date convertedDate = Date.valueOf(date);
		
		sock.Send(new BookRequest(Code.BOOK_REQUEST, userId, excursionId, convertedDate, passengerCount).GetSerialized());
		
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		Response response = (Response) os.readObject();
		
		os.close();
		sock.Close();
		
		return response.getCode();
	}
	
	public static BookedExcursionListResponse GetUserExcursions(int userId) throws IOException, ClassNotFoundException {
		SocketInstance sock = SocketInstance.getInstance();
		
		sock.Send(new IndividualRequest(Code.GET_USER_EXCURSIONS, userId).GetSerialized());
		
		byte[] response = sock.Receive();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(response);
		ObjectInputStream os = new ObjectInputStream(bais);
		
		BookedExcursionListResponse res = (BookedExcursionListResponse) os.readObject();
		
		os.close();
		sock.Close();
		
		return res;
	}
	
	public static Code EditBooking(int bookingId, int passengers, LocalDate date) throws IOException, ClassNotFoundException {
		SocketInstance sock = SocketInstance.getInstance();
		
		Date convertedDate = Date.valueOf(date);
		
		sock.Send(new BookingEditRequest(Code.EDIT_REQUEST, bookingId, passengers, convertedDate).GetSerialized());
		
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		Response response = (Response) os.readObject();
		
		os.close();
		sock.Close();
		
		return response.getCode();
	}
	
	public static Code CancelBooking(int booking) throws IOException, ClassNotFoundException {
		SocketInstance sock = SocketInstance.getInstance();
		
		sock.Send(new IndividualRequest(Code.CANCEL_REQUEST, booking).GetSerialized());
		
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		Response response = (Response) os.readObject();
		
		os.close();
		sock.Close();
		
		return response.getCode();
	}
}
